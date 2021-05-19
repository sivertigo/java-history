/*
 * @(#)ObjectStreamClass.java	1.80 02/02/28
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.io;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.DigestOutputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Modifier;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Constructor;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;

import sun.misc.SoftCache;

/**
 * Serialization's descriptor for classes.
 * It contains the name and serialVersionUID of the class.
 * <br>
 * The ObjectStreamClass for a specific class loaded in this Java VM can
 * be found/created using the lookup method.<p>
 * The algorithm to compute the SerialVersionUID is described in 
 * <a href="http://java.sun.com/products/jdk/1.2/docs/guide/serialization/spec/class.doc4.html"> Object Serialization Specification, Section 4.4, Stream Unique Identifiers</a>.
 *
 * @author  Roger Riggs
 * @version @(#)ObjectStreamClass.java	1.45 97/08/03
 * @see ObjectStreamField
 * @see <a href="http://java.sun.com/products/jdk/1.2/docs/guide/serialization/spec/class.doc.html"> Object Serialization Specification, Section 4, Class Descriptors</a>
 * @since   JDK1.1
 */
public class ObjectStreamClass implements java.io.Serializable {

    /** 
     * Find the descriptor for a class that can be serialized. 
     * Creates an ObjectStreamClass instance if one does not exist 
     * yet for class. Null is returned if the specified class does not 
     * implement java.io.Serializable or java.io.Externalizable.
     */
    public static ObjectStreamClass lookup(Class cl)
    {
	ObjectStreamClass desc = lookupInternal(cl);
	if (desc.isSerializable() || desc.isExternalizable())
	    return desc;
	return null;
    }
    /*
     * Find the class descriptor for the specified class.
     * Package access only so it can be called from ObjectIn/OutStream.
     */
    static ObjectStreamClass lookupInternal(Class cl)
    {
	/*
	 * Note: using the class directly as the key for storing entries does
	 * not pin the class indefinitely, since SoftCache removes strong refs
	 * to keys when the corresponding values are gc'ed.
	 */
	Object entry;
	EntryFuture future = null;
	synchronized (localDescs) {
	    if ((entry = localDescs.get(cl)) == null) {
		localDescs.put(cl, future = new EntryFuture());
	    }
	}
	
	if (entry instanceof ObjectStreamClass) {  // check common case first
	    return (ObjectStreamClass) entry;
	} else if (entry instanceof EntryFuture) {
	    entry = ((EntryFuture) entry).get();
	} else if (entry == null) {
	    try {
			entry = createLocalDescriptor(cl);
	    } catch (Throwable th) {
			entry = th;
	    }
	    future.set(entry);
	    synchronized (localDescs) {
		localDescs.put(cl, entry);
	    }
	}

	if (entry instanceof ObjectStreamClass) {
	    return (ObjectStreamClass) entry;
	} else if (entry instanceof RuntimeException) {
	    throw (RuntimeException) entry;
	} else if (entry instanceof Error) {
	    throw (Error) entry;
	} else {
	    throw new InternalError("unexpected entry: " + entry);
	}
    }

    /*
     * Creates local class descriptor for the given class.
     */
    private static ObjectStreamClass createLocalDescriptor(Class cl) {
	/* Check if it's serializable */
	boolean serializable = Serializable.class.isAssignableFrom(cl);

	/* If the class is only Serializable,
	 * lookup the descriptor for the superclass.
	 */
	ObjectStreamClass superdesc = null;
	if (serializable) {
	Class superclass = cl.getSuperclass();
	if (superclass != null) 
	    superdesc = lookup(superclass);
	}

	/* Check if its' externalizable.
	 * If it's Externalizable, clear the serializable flag.
	 * Only one or the other may be set in the protocol.
	 */
	boolean externalizable = false;
	if (serializable) {
		externalizable = 
		    ((superdesc != null) && superdesc.isExternalizable()) ||
		    Externalizable.class.isAssignableFrom(cl);
		if (externalizable) {
		    serializable = false;
		}
	}

	/* Create a new version descriptor,
	 */
	return new ObjectStreamClass(cl, superdesc,	
				      serializable, externalizable);
	}
    
    /**
     * The name of the class described by this descriptor.
     */
    public String getName() {
	return name;
    }

    /**
     * Return the serialVersionUID for this class.
     * The serialVersionUID defines a set of classes all with the same name
     * that have evolved from a common root class and agree to be serialized
     * and deserialized using a common format.
     * NonSerializable classes have a serialVersionUID of 0L.
     */
    public long getSerialVersionUID() {
	return suid;
    }

    /**
     * Return the class in the local VM that this version is mapped to.
     * Null is returned if there is no corresponding local class.
     */
    public Class forClass() {
	return ofClass;
    }

    /**
     * Return an array of the fields of this serializable class.
     * @return an array containing an element for each persistent
     * field of this class. Returns an array of length zero if
     * there are no fields.
     * @since JDK1.2
     */
    public ObjectStreamField[] getFields() {
    	// Return a copy so the caller can't change the fields.
	if (fields.length > 0) {
	    ObjectStreamField[] dup = new ObjectStreamField[fields.length];
	    System.arraycopy(fields, 0, dup, 0, fields.length);
	    return dup;
	} else {
	    return fields;
	}
    }

    /* Avoid unnecessary allocations within package. */
    final ObjectStreamField[] getFieldsNoCopy() {
	return fields;
    }

    /**
     * Get the field of this class by name.
     * @return The ObjectStreamField object of the named field or null if there
     * is no such named field.
     */
    public ObjectStreamField getField(String name) {
	ObjectStreamField searchKey = 
	    ObjectStreamField.constructSearchKey(name, Byte.TYPE);

	int index = -1;
	if (objFields != fields.length) {
	    // perform binary search over primitive fields.
	    index = Arrays.binarySearch(fields, searchKey);
	}

	if (index < 0 && objFields > 0) {
	    // perform binary search over object fields.
	    searchKey.setSearchKeyTypeString(true);
	    index = Arrays.binarySearch(fields, searchKey);
	}
	return (index < 0) ? null : fields[index];
    }


    /**
     * Get the field of this class by name and fieldType.
     * @return The ObjectStreamField object of the named field, type
     *         or null if there is no such named field of fieldType.
     */
    ObjectStreamField getField(String name, Class fieldType) {
	ObjectStreamField searchKey = 
	    ObjectStreamField.constructSearchKey(name, fieldType);
	int index = Arrays.binarySearch(fields, searchKey);
	return (index < 0) ? null : fields[index];
    }

    /**
     * Return a string describing this ObjectStreamClass.
     */
    public String toString() {
	StringBuffer sb = new StringBuffer();

	sb.append(name);
	sb.append(": static final long serialVersionUID = ");
	sb.append(Long.toString(suid));
	sb.append("L;");
	return sb.toString();
    }

    /*
     * Create a new ObjectStreamClass from a loaded class.
     * Don't call this directly, call lookup instead.
     */
    private ObjectStreamClass(final Class cl, ObjectStreamClass superdesc,
			      boolean serial, boolean extern)
    {
	ofClass = cl;		/* created from this class */

	name = cl.getName();
	superclass = superdesc;
	serializable = serial;
	externalizable = extern;

	if (!serializable || externalizable) {
	    fields = NO_FIELDS;
	} else if (serializable) {
	    /* Ask for permission to override field access checks.
	     */
	    AccessController.doPrivileged(new PrivilegedAction() {
		public Object run() {
		
		    /* Fill in the list of persistent fields.
		     * If it is declared, use the declared serialPersistentFields.
		     * Otherwise, extract the fields from the class itself.
		     */
		    try {
			Field pf = cl.getDeclaredField("serialPersistentFields");
			pf.setAccessible(true);
			ObjectStreamField[] f = (ObjectStreamField[])pf.get(cl);
			int mods = pf.getModifiers();
			//field must be private for security reasons.
			if (Modifier.isPrivate(mods)) {
			    fields = f;
			}
		    } catch (NoSuchFieldException e) {
			fields = null;
		    } catch (IllegalAccessException e) {
			fields = null;
		    } catch (IllegalArgumentException e) {
			fields = null;
		    }

		    if (fields == null) {
			/* Get all of the declared fields for this
			 * Class. setAccessible on all fields so they
			 * can be accessed later.  Create a temporary
			 * ObjectStreamField array to hold each
			 * non-static, non-transient field. Then copy the
			 * temporary array into an array of the correct
			 * size once the number of fields is known.
			 */
			Field[] actualfields = cl.getDeclaredFields();
			AccessibleObject.setAccessible(actualfields, true);

			int numFields = 0;
			ObjectStreamField[] tempFields = 
			    new ObjectStreamField[actualfields.length];
			for (int i = 0; i < actualfields.length; i++) {
			    int modifiers = actualfields[i].getModifiers();
			    if (!Modifier.isStatic(modifiers) &&
				!Modifier.isTransient(modifiers)) {
				tempFields[numFields++] =
				    new ObjectStreamField(actualfields[i]);
			    }
			}
			fields = new ObjectStreamField[numFields];
			System.arraycopy(tempFields, 0, fields, 0, numFields);
			
		    } else {
			// For each declared persistent field, look for an actual
			// reflected Field. If there is one, make sure it's the correct
			// type and cache it in the ObjectStreamClass for that field.
			for (int j = fields.length-1; j >= 0; j--) {
			    try {
				Field reflField = cl.getDeclaredField(fields[j].getName());
				if (fields[j].getType() == reflField.getType()) {
				    reflField.setAccessible(true);
				    fields[j].setField(reflField);
				} else {
				    // TBD: Should this be flagged as an error?
				}
			    } catch (NoSuchFieldException e) {
				// Nothing to do
			    }
			}
		    }
		    return null;
		}
	    });

	    if (fields.length > 1)
		Arrays.sort(fields);

	    /* Set up field data for use while writing using the API api. */
	    computeFieldInfo();
	}

	/* Get the serialVersionUID from the class.
	 * It uses the access override mechanism so make sure
	 * the field objects is only used here.
	 *
	 * NonSerializable classes have a serialVerisonUID of 0L.
	 */
	if (isNonSerializable()) {
	    suid = 0L;
	} else {

	    // Lookup special Serializable members using reflection.
	    AccessController.doPrivileged(new PrivilegedAction() {
		public Object run() {
		    try {
			Field f = cl.getDeclaredField("serialVersionUID");
			int mods = f.getModifiers();
			if (Modifier.isStatic(mods) && Modifier.isFinal(mods)) {
			    f.setAccessible(true);
			    suid = f.getLong(cl);
			} else {
			    suid = computeSerialVersionUID(cl);
			}
		    } catch (NoSuchFieldException ex) {
			suid = computeSerialVersionUID(cl);
		    } catch (IllegalAccessException ex) {
			suid = computeSerialVersionUID(cl);
		    }
	    
		    /* check for class provided substitution methods, 
		     * writeReplace and readResolve. Methods can not
		     * be static.
		     */
		    writeReplaceMethod = 
			getDeclaredMethod("writeReplace", NULL_ARGS,
					  0, Modifier.STATIC);
		    if (writeReplaceMethod == null && superclass != null &&
			checkSuperMethodAccess(superclass.writeReplaceMethod)) {
			writeReplaceMethod = superclass.writeReplaceMethod;
		    }
		
		    readResolveMethod = 
			getDeclaredMethod("readResolve", NULL_ARGS,
					  0, Modifier.STATIC);
		    if (readResolveMethod == null && superclass != null &&
			checkSuperMethodAccess(superclass.readResolveMethod)) {
			readResolveMethod = superclass.readResolveMethod;
		    }
	    
		    /* Cache lookup of writeObject and readObject for 
		     * Serializable classes. (Do not lookup for Externalizable)
		     */
		    if (serializable) { 

			//Workaround compiler bug. See declaration for more detail.
			if (OOS_ARGS == null || OIS_ARGS == null) {
			    initStaticMethodArgs();
			}
			//end Workaround.
		    
			writeObjectMethod = 
			    getDeclaredMethod("writeObject", OOS_ARGS,
					  Modifier.PRIVATE, Modifier.STATIC);
			if (writeObjectMethod != null) {
			    hasWriteObjectMethod = true;
			}
			readObjectMethod = 
			    getDeclaredMethod("readObject", OIS_ARGS,
					      Modifier.PRIVATE, Modifier.STATIC);
		    }
		    return null;
		}
	    });
	}
    }

    /*
     * Create an empty ObjectStreamClass for a class about to be read.
     * This is separate from read so ObjectInputStream can assign the
     * wire handle early, before any nested ObjectStreamClass might
     * be read.
     */
    ObjectStreamClass(String n, long s) {
	name = n;
	suid = s;
	superclass = null;
    }

    /* Validate the compatibility of the stream class descriptor and 
     * the specified local class.
     *
     * @exception InvalidClassException if stream and local class are 
     *                                  not compatible.
     */
    private void validateLocalClass(Class localCl) throws InvalidClassException {
	if (localClassDesc == null)
	    throw new InvalidClassException(localCl.getName(), 
					    "Local class not compatible");

	if (suid != localClassDesc.suid) {

	    /* Check for exceptional cases that allow mismatched suid. */

	    /* Allow adding Serializable or Externalizable
	     * to a later release of the class. 
	     */
	    boolean addedSerialOrExtern = 
		isNonSerializable() || localClassDesc.isNonSerializable();

	    /* Disregard the suid of an array when name and localCl.Name differ. 
	     * If resolveClass() returns an array with a different package 
	     * name, the serialVersionUIDs will not match since the fully
	     * qualified array class is used in the
	     * computation of the array's serialVersionUID. There is
	     * no way to set a permanent serialVersionUID for an array type.
	     */
	    boolean arraySUID = (localCl.isArray() && ! localCl.getName().equals(name));

	    if (! arraySUID && ! addedSerialOrExtern ) {
		throw new InvalidClassException(localCl.getName(), 
		    "Local class not compatible:" + 
		    " stream classdesc serialVersionUID=" + suid +
		    " local class serialVersionUID=" + localClassDesc.suid);
	    }
	    
	}

	/* compare the class names, stripping off package names. */
	if (! compareClassNames(name, localCl.getName(), '.'))
	    throw new InvalidClassException(localCl.getName(),
			 "Incompatible local class name. " +
	        	 "Expected class name compatible with " + 
			 name);

	/*
	 * Test that both implement either serializable or externalizable.
	 */
	if ((serializable && localClassDesc.externalizable) ||
	    (externalizable && localClassDesc.serializable))
	    throw new InvalidClassException(localCl.getName(),
					"Serializable is incompatible with Externalizable");

    }

    /*
     * Set the local class that this stream class descriptor matches.
     * The base class name and serialization version id must match if
     * both classes are serializable.
     * Fill in the reflected Fields that will be used for reading.
     */
    void setClass(Class cl) throws InvalidClassException {

	/* Allow no local class implementation. Must be able to 
	 * skip objects in stream introduced by class evolution.
	 */
	if (cl == null) {
	    localClassDesc = null;
	    ofClass = null;
	    computeFieldInfo();
	    return;
	}

	localClassDesc = lookupInternal(cl);
	validateLocalClass(cl);

	/* Disable instance deserialization when one class is serializable 
	 * and the other is not or if both the classes are neither serializable
	 * nor externalizable. 
	 */
	if ((serializable != localClassDesc.serializable) ||
	    (externalizable != localClassDesc.externalizable) || 
	    (!serializable && !externalizable)) {

	    /* Delay signaling InvalidClassException until trying 
             * to deserialize an instance of this class. Allows
	     * a previously nonSerialized class descriptor that was written 
             * into the stream to be made Serializable
	     * or Externalizable, in a later release.
	     */
	    disableInstanceDeserialization = true;
	    ofClass = cl;
	    return;
	}

	/* Set up the reflected Fields in the class where the value of each 
	 * field in this descriptor should be stored.
	 * Each field in this ObjectStreamClass (the source) is located (by 
	 * name) in the ObjectStreamClass of the class(the destination).
	 * In the usual (non-versioned case) the field is in both
	 * descriptors and the types match, so the reflected Field is copied.
	 * If the type does not match, a InvalidClass exception is thrown.
	 * If the field is not present in the class, the reflected Field 
	 * remains null so the field will be read but discarded.
	 * If extra fields are present in the class they are ignored. Their
	 * values will be set to the default value by the object allocator.
	 * Both the src and dest field list are sorted by type and name.
	 */

	ObjectStreamField[] destfield = 
	    (ObjectStreamField[])localClassDesc.fields;
	ObjectStreamField[] srcfield = 
	    (ObjectStreamField[])fields;

	int j = 0;
    nextsrc:
	for (int i = 0; i < srcfield.length; i++ ) {
	    /* Find this field in the dest*/
	    for (int k = j; k < destfield.length; k++) {
	      if (srcfield[i].getName().equals(destfield[k].getName())) {
		  /* found match */
		  if (srcfield[i].isPrimitive() && 
		      !srcfield[i].typeEquals(destfield[k])) {
		      throw new InvalidClassException(cl.getName(),
						  "The type of field " +
						       destfield[i].getName() +
						       " of class " + name +
						       " is incompatible.");
		  }

		  /* Skip over any fields in the dest that are not in the src */
 		  j = k; 
		  
		  srcfield[i].setField(destfield[j].getField());
		  // go on to the next source field
		  continue nextsrc;
	      }
	    }
	}

	/* Set up field data for use while reading from the input stream. */
	computeFieldInfo();

	/* Remember the class this represents */
	ofClass = cl;

	/* get the cache of these methods from the local class 
	 * implementation. 
	 */
	readObjectMethod = localClassDesc.readObjectMethod;
	readResolveMethod = localClassDesc.readResolveMethod;
    }

    /* Compare the base class names of streamName and localName.
     * 
     * @return  Return true iff the base class name compare.
     * @parameter streamName	Fully qualified class name.
     * @parameter localName	Fully qualified class name.
     * @parameter pkgSeparator	class names use either '.' or '/'.
     * 
     * Only compare base class name to allow package renaming.
     */
    static boolean compareClassNames(String streamName,
					 String localName,
					 char pkgSeparator) {
	/* compare the class names, stripping off package names. */
	int streamNameIndex = streamName.lastIndexOf(pkgSeparator);
	if (streamNameIndex < 0) 
	    streamNameIndex = 0;

	int localNameIndex = localName.lastIndexOf(pkgSeparator);
	if (localNameIndex < 0)
	    localNameIndex = 0;

	return streamName.regionMatches(false, streamNameIndex, 
					localName, localNameIndex,
					streamName.length() - streamNameIndex);
    }

    /*
     * Compare the types of two class descriptors.
     * They match if they have the same class name and suid
     */
    boolean typeEquals(ObjectStreamClass other) {
	return (suid == other.suid) &&
	    compareClassNames(name, other.name, '.');
    }
    
    /*
     * Return the superclass descriptor of this descriptor.
     */
    void setSuperclass(ObjectStreamClass s) {
	superclass = s;
    }

    /*
     * Return the superclass descriptor of this descriptor.
     */
    ObjectStreamClass getSuperclass() {
	return superclass;
    }
    
    /*
     * Return whether the class has a writeObject method
     */
    boolean hasWriteObject() {
	return hasWriteObjectMethod;
    }
    
    /*
     * Return true if all instances of 'this' Externalizable class 
     * are written in block-data mode from the stream that 'this' was read
     * from. <p>
     *
     * In JDK 1.1, all Externalizable instances are not written 
     * in block-data mode.
     * In JDK 1.2, all Externalizable instances, by default, are written
     * in block-data mode and the Externalizable instance is terminated with
     * tag TC_ENDBLOCKDATA. Change enabled the ability to skip Externalizable 
     * instances.
     *
     * IMPLEMENTATION NOTE:
     *   This should have been a mode maintained per stream; however,
     *   for compatibility reasons, it was only possible to record
     *   this change per class. All Externalizable classes within
     *   a given stream should either have this mode enabled or 
     *   disabled. This is enforced by not allowing the PROTOCOL_VERSION
     *   of a stream to he changed after any objects have been written.
     *
     * @see ObjectOuputStream#useProtocolVersion
     * @see ObjectStreamConstants#PROTOCOL_VERSION_1
     * @see ObjectStreamConstants#PROTOCOL_VERSION_2
     *
     * @since JDK 1.2
     */
    boolean hasExternalizableBlockDataMode() {
	return hasExternalizableBlockData;
    }

    /*
     * Return the ObjectStreamClass of the local class this one is based on.
     */
    ObjectStreamClass localClassDescriptor() {
	return localClassDesc;
    }
    
    /*
     * Get the Serializability of the class.
     */
    boolean isSerializable() {
	return serializable;
    }

    /*
     * Get the externalizability of the class.
     */
    boolean isExternalizable() {
	return externalizable;
    }

    boolean isNonSerializable() {
	return ! (externalizable || serializable);
    }

    /*
     * Calculate the size of the array needed to store primitive data and the
     * number of object references to read when reading from the input 
     * stream.
     */
    private void computeFieldInfo() {
	primBytes = 0;
	objFields = 0;

	for (int i = 0; i < fields.length; i++ ) {
	    switch (fields[i].getTypeCode()) {
	    case 'B':
	    case 'Z':
	    	fields[i].setOffset(primBytes);
	    	primBytes += 1;
	    	break;
	    case 'C':
	    case 'S': 
		fields[i].setOffset(primBytes);
	    	primBytes += 2;
	    	break;

	    case 'I':
	    case 'F': 
	    	fields[i].setOffset(primBytes);
	    	primBytes += 4;
	    	break;
	    case 'J':
	    case 'D' :
		fields[i].setOffset(primBytes);
	    	primBytes += 8;
	    	break;
	    
	    case 'L':
	    case '[':
	    	fields[i].setOffset(objFields);
	    	objFields += 1;
	    	break;
	    }
	}
    }
    
    /*
     * Compute a hash for the specified class.  Incrementally add
     * items to the hash accumulating in the digest stream.
     * Fold the hash into a long.  Use the SHA secure hash function.
     */
    private static long computeSerialVersionUID(Class cl) {
	ByteArrayOutputStream devnull = new ByteArrayOutputStream(512);

	long h = 0;
	try {
	    MessageDigest md = MessageDigest.getInstance("SHA");
	    DigestOutputStream mdo = new DigestOutputStream(devnull, md);
	    DataOutputStream data = new DataOutputStream(mdo);


	    data.writeUTF(cl.getName());
	    
	    int classaccess = cl.getModifiers();
	    classaccess &= (Modifier.PUBLIC | Modifier.FINAL |
			    Modifier.INTERFACE | Modifier.ABSTRACT);

	    /* Workaround for javac bug that only set ABSTRACT for
	     * interfaces if the interface had some methods.
	     * The ABSTRACT bit reflects that the number of methods > 0.
	     * This is required so correct hashes can be computed
	     * for existing class files.
	     * Previously this hack was previously present in the VM.
	     */
	    Method[] method = cl.getDeclaredMethods();
	    if ((classaccess & Modifier.INTERFACE) != 0) {
		classaccess &= (~Modifier.ABSTRACT);
		if (method.length > 0) {
		    classaccess |= Modifier.ABSTRACT;
		}
	    }

	    data.writeInt(classaccess);

	    /* 
	     * Get the list of interfaces supported,
	     * Accumulate their names in Lexical order
	     * and add them to the hash
	     */
	    if (!cl.isArray()) {
		/* In JDK1.2fcs, getInterfaces() was modified to return
		 * {java.lang.Cloneable, java.io.Serializable} when
		 * called on array classes.  These values would upset
		 * the computation of the hash, so we explicitly omit
		 * them from its computation.
		 */
		Class interfaces[] = cl.getInterfaces();
		Arrays.sort(interfaces, compareClassByName);
		
		for (int i = 0; i < interfaces.length; i++) {
		    data.writeUTF(interfaces[i].getName());
		}
	    }

	    /* Sort the field names to get a deterministic order */
	    Field[] field = cl.getDeclaredFields();
	    Arrays.sort(field, compareMemberByName);

	    for (int i = 0; i < field.length; i++) {
		Field f = field[i];

		/* Include in the hash all fields except those that are
		 * private transient and private static.
		 */
		int m = f.getModifiers();
		if (Modifier.isPrivate(m) && 
		    (Modifier.isTransient(m) || Modifier.isStatic(m)))
		    continue;

		data.writeUTF(f.getName());
		data.writeInt(m);
		data.writeUTF(getSignature(f.getType()));
	    }

	    if (hasStaticInitializer(cl)) {
		data.writeUTF("<clinit>");
		data.writeInt(Modifier.STATIC); // TBD: what modifiers does it have
		data.writeUTF("()V");
	    }

	    /*
	     * Get the list of constructors including name and signature
	     * Sort lexically, add all except the private constructors
	     * to the hash with their access flags
	     */

	    MethodSignature[] constructors =
		MethodSignature.removePrivateAndSort(cl.getDeclaredConstructors());
	    for (int i = 0; i < constructors.length; i++) {
		MethodSignature c = constructors[i];
		String mname = "<init>";
		String desc = c.signature;
		desc = desc.replace('/', '.');
		data.writeUTF(mname);
		data.writeInt(c.member.getModifiers());
		data.writeUTF(desc);
	    }

	    /* Include in the hash all methods except those that are
	     * private transient and private static.
	     */
	    MethodSignature[] methods =
		MethodSignature.removePrivateAndSort(method);
	    for (int i = 0; i < methods.length; i++ ) {
		MethodSignature m = methods[i];	
		String desc = m.signature;
		desc = desc.replace('/', '.');
		data.writeUTF(m.member.getName());
		data.writeInt(m.member.getModifiers());
		data.writeUTF(desc);
	    }

	    /* Compute the hash value for this class.
	     * Use only the first 64 bits of the hash.
	     */
	    data.flush();
	    byte hasharray[] = md.digest();
	    for (int i = 0; i < Math.min(8, hasharray.length); i++) {
		h += (long)(hasharray[i] & 255) << (i * 8);
	    }
	} catch (IOException ignore) {
	    /* can't happen, but be deterministic anyway. */
	    h = -1;
	} catch (NoSuchAlgorithmException complain) {
	    throw new SecurityException(complain.getMessage());
	}
	return h;
    }


    /**
      * Compute the JVM signature for the class.
      */
    static String getSignature(Class clazz) {
	String type = null;
	if (clazz.isArray()) {
	    Class cl = clazz;
	    int dimensions = 0;
	    while (cl.isArray()) {
		dimensions++;
		cl = cl.getComponentType();
	    }
	    StringBuffer sb = new StringBuffer();
	    for (int i = 0; i < dimensions; i++) {
		sb.append("[");
	    }
	    sb.append(getSignature(cl));
	    type = sb.toString();
	} else if (clazz.isPrimitive()) {
	    if (clazz == Integer.TYPE) {
		type = "I";
	    } else if (clazz == Byte.TYPE) {
		type = "B";
	    } else if (clazz == Long.TYPE) {
		type = "J";
	    } else if (clazz == Float.TYPE) {
		type = "F";
	    } else if (clazz == Double.TYPE) {
		type = "D";
	    } else if (clazz == Short.TYPE) {
		type = "S";
	    } else if (clazz == Character.TYPE) {
		type = "C";
	    } else if (clazz == Boolean.TYPE) {
		type = "Z";
	    } else if (clazz == Void.TYPE) {
		type = "V";
	    }
	} else {
	    type = "L" + clazz.getName().replace('.', '/') + ";";
	}
	return type;
    }

    /*
     * Compute the JVM method descriptor for the method.
     */
    static String getSignature(Method meth) {
	StringBuffer sb = new StringBuffer();

	sb.append("(");

	Class[] params = meth.getParameterTypes(); // avoid clone
	for (int j = 0; j < params.length; j++) {
	    sb.append(getSignature(params[j]));
	}
	sb.append(")");
	sb.append(getSignature(meth.getReturnType()));
	return sb.toString();
    }

    /*
     * Compute the JVM constructor descriptor for the constructor.
     */
    static String getSignature(Constructor cons) {
	StringBuffer sb = new StringBuffer();

	sb.append("(");

	Class[] params = cons.getParameterTypes(); // avoid clone
	for (int j = 0; j < params.length; j++) {
	    sb.append(getSignature(params[j]));
	}
	sb.append(")V");
	return sb.toString();
    }


    /*
     * locate the ObjectStreamClass for this class and write it to the stream.
     *
     * @serialData
     *  <code>primitive UTF-8 String</code>  Qalified class name.
     *  <code>long</code>  Serial version unique identifier for compatible classes.
     *	<code>byte</code>. Mask with <code>java.io.ObjectStreamConstants.SC_*</code>.<br>
     *	<code>short</code>. Number of Serializable fields to follow. If 0, no more data.
     *  <code>list of Serializable Field descriptors</code>. Descriptors for Primitive 
     *       typed fields are written first sorted by field name 
     *       followed by descriptors for the object typed fields sorted 
     *       by field name. The names are sorted using String.compareTo.
     *
     *  A Serializable field consists of the following data:
     *   <code>byte</code>  TypeCode of field. See ObjectStreamField.getTypeCode().
     *   <code>primitive UTF-8 encoded String</code>  Unqualified name of field.
     *   <code>String</code>   Qualified class name. 
     */
    void write(ObjectOutputStream s) throws IOException {
	
	/* write the flag indicating that this class has write/read object methods */
	int flags = 0;
	if (hasWriteObjectMethod)
	    flags |= ObjectStreamConstants.SC_WRITE_METHOD;
	if (serializable)
	    flags |= ObjectStreamConstants.SC_SERIALIZABLE;
	if (externalizable) {
	    flags |=  ObjectStreamConstants.SC_EXTERNALIZABLE;

	    /* Enabling the SC_BLOCK_DATA flag indicates PROTCOL_VERSION_2.*/
	    if (! s.useDeprecatedExternalizableFormat)
		flags |= ObjectStreamConstants.SC_BLOCK_DATA;
        }
	s.writeByte(flags);
	
	// If there are no fields, write a null and return
	if (fields == null) {
	    s.writeShort(0);
	    return;
	}

	/* write the total number of fields */
	s.writeShort(fields.length);
	
	/* Write out the descriptors of the primitive fields Each
	 * descriptor consists of the UTF fieldname, a short for the
	 * access modes, and the first byte of the signature byte.
	 * For the object types, ('[' and 'L'), a reference to the
	 * type of the field follows.
	 */
	for (int i = 0; i < fields.length; i++ ) {
	    ObjectStreamField f = fields[i];
	    s.writeByte(f.getTypeCode());
	    s.writeUTF(f.getName());
	    if (!f.isPrimitive()) {
		s.writeTypeString(f.getTypeString());
	    }
	}
    }

    /*
     * Read the version descriptor from the stream.
     * Write the count of field descriptors
     * for each descriptor write the first character of its type,
     * the name of the field.
     * If the type is for an object either array or object, write
     * the type typedescriptor for the type
     */
    void read(ObjectInputStream s) throws IOException, ClassNotFoundException {
	
	/* read flags and determine whether the source class had
         * write/read methods.
	 */
	byte flags = s.readByte();
	serializable = (flags & ObjectStreamConstants.SC_SERIALIZABLE) != 0;
	externalizable = (flags & ObjectStreamConstants.SC_EXTERNALIZABLE) != 0;
	hasWriteObjectMethod = serializable ?
	    (flags & ObjectStreamConstants.SC_WRITE_METHOD) != 0 :
	    false;
	hasExternalizableBlockData = externalizable ? 
	    (flags & ObjectStreamConstants.SC_BLOCK_DATA) != 0 :
	    false;
	                 

	/* Read the number of fields described.
	 * For each field read the type byte, the name.
	 */    
	int count = s.readShort();
	fields = new ObjectStreamField[count];

	/* disable replacement of String objects read
	 * by ObjectStreamClass. */
	boolean prevEnableResolve = s.enableResolve;
	s.enableResolve = false;
	try {
	    for (int i = 0; i < count; i++ ) {
		char type = (char)s.readByte();
		String name = s.readUTF();
		String ftype = null;
		if (type == '[' || type == 'L') {
		    ftype = (String)s.readObject();
		}
		fields[i] = 
		    new ObjectStreamField(name, type, null, ftype);
	    }
	} finally {
	    s.enableResolve = prevEnableResolve;
	}
    }

    /* To accomodate nonSerializable classes written into a stream,
     * this check must be delayed until an instance is deserialized.
     */
    void verifyInstanceDeserialization() throws InvalidClassException {
	if (disableInstanceDeserialization) {
	    String name = (serializable || externalizable) ? 
  		              localClassDesc.getName() : getName();
	    String stype = (serializable || localClassDesc.serializable) ? 
			  "Serializable" : 
			  (externalizable || localClassDesc.externalizable) ?
			  "Externalizable" : "Serializable or Externalizable";
	    throw new InvalidClassException(name, "is not " + stype);
	}
    }

    /**
     * Placeholder used in class descriptor lookup table for an entry in the
     * process of being initialized.  (Internal) callers which receive an
     * EntryFuture as the result of a lookup should call the get() method of
     * the EntryFuture; this will return the actual entry once it is ready for
     * use and has been set().  To conserve objects, EntryFutures synchronize
     * on themselves.
     */  
    private static class EntryFuture {

	private static final Object unset = new Object();
	private Object entry = unset;

	synchronized void set(Object entry) {
	    if (this.entry != unset) {
		throw new IllegalStateException();
	    }
	    this.entry = entry;
	    notifyAll();
	}

	synchronized Object get() {
	    boolean interrupted = false;
	    while (entry == unset) {
		try {
		    wait();
		} catch (InterruptedException ex) {
		    interrupted = true;
		}
	    }
	    if (interrupted) {
		AccessController.doPrivileged(
		    new PrivilegedAction() {
			public Object run() {
			    Thread.currentThread().interrupt();
			    return null;
			}
		    }
		);
	    }
	    return entry;
	}
    }


    /*
     * Cache of Class -> ClassDescriptor Mappings.
     */
    private static final SoftCache localDescs = new SoftCache(10);

    /*
     * The name of this descriptor
     */
    private String name;
    
    /*
     * The descriptor of the supertype.
     */
    private ObjectStreamClass superclass;

    /*
     * Flags for Serializable and Externalizable.
     */
    private boolean serializable;
    private boolean externalizable;
    
    /*
     * Array of persistent fields of this class, sorted by
     * type and name.
     */
    private ObjectStreamField[] fields;
    
    /*
     * Class that is a descriptor for in this virtual machine.
     */
    private Class ofClass;
    
    /* 
     * SerialVersionUID for this class.
     */
    private long suid;
    
    /*
     * The total number of bytes of primitive fields.
     * The total number of object fields.
     */
    int primBytes;
    int objFields;

    /* True if this class has/had a writeObject method */
    private boolean hasWriteObjectMethod;

    /* In JDK 1.1, external data was not written in block mode.
     * As of JDK 1.2, external data is written in block data mode. This
     * flag enables JDK 1.2 to be able to read JDK 1.1 written external data.
     *
     * @since JDK 1.2
     */
    private boolean hasExternalizableBlockData;
    Method writeObjectMethod;
    Method readObjectMethod;
    Method readResolveMethod;
    Method writeReplaceMethod;

    /*
     * ObjectStreamClass that this one was built from.
     */
    private ObjectStreamClass localClassDesc;
    
    /* Indicates that stream and local class are not both
     * serializable. No instances of this class can be deserialized.
     */
    private boolean disableInstanceDeserialization = false;

    /* Find out if the class has a static class initializer <clinit> */
    private static native boolean hasStaticInitializer(Class cl);

    /** use serialVersionUID from JDK 1.1. for interoperability */
    private static final long serialVersionUID = -6120832682080437368L;

    /**
     * Set serialPersistentFields of a Serializable class to this value to 
     * denote that the class has no Serializable fields. 
     */
    public static final ObjectStreamField[] NO_FIELDS = 
	new ObjectStreamField[0];

    /**
     * Class ObjectStreamClass is special cased within the 
     * Serialization Stream Protocol. 
     *
     * An ObjectStreamClass is written intially into an ObjectOutputStream 
     * in the following format:
     * <pre>
     *      TC_CLASSDESC className, serialVersionUID, flags, 
     *                   length, list of field descriptions.
     *
     * FIELDNAME        TYPES
     *                  DESCRIPTION
     * --------------------------------------
     * className        primitive data String
     *                  Fully qualified class name.
     *
     * serialVersionUID long
     *                  Stream Unique Identifier for compatible classes
     *                  with same base class name.
     *
     * flags            byte
     *                  Attribute bit fields defined in 
     *                  <code>java.io.ObjectStreamConstants.SC_*</code>.
     *
     * length           short
     *                  The number of field descriptions to follow.
     *
     * fieldDescription (byte, primitive data String, String Object)
     *                  A pseudo-externalized format of class
     *                  <code>java.io.ObjectStreamField</code>.
     *                  Consists of typeCode, fieldName, and,
     *                  if a nonPrimitive typecode, a fully qualified
     *                  class name. See <code>Class.getName</code> method 
     *                  for the typecode byte encodings.
     * </pre>
     * The first time the class descriptor
     * is written into the stream, a new handle is generated.
     * Future references to the class descriptor are
     * written as references to the initial class descriptor instance.
     *
     * @see java.io.ObjectOutputStream#writeUTF(java.lang.String)
     */
    private static final ObjectStreamField[] serialPersistentFields = 
	NO_FIELDS;

    /*
     * Entries held in the Cache of known ObjectStreamClass objects.
     * Entries are chained together with the same hash value (modulo array size).
     */
    private static class ObjectStreamClassEntry extends java.lang.ref.SoftReference
    {
	ObjectStreamClassEntry(ObjectStreamClass c) {
	    super(c);
	}
	ObjectStreamClassEntry next;
    }

    /*
     * Comparator object for Classes and Interfaces
     */
    private static Comparator compareClassByName =
    	new CompareClassByName();

    private static class CompareClassByName implements Comparator {
	public int compare(Object o1, Object o2) {
	    Class c1 = (Class)o1;
	    Class c2 = (Class)o2;
	    return (c1.getName()).compareTo(c2.getName());
	}
    }

    /*
     * Comparator object for Members, Fields, and Methods
     */
    private static Comparator compareMemberByName =
    	new CompareMemberByName();

    private static class CompareMemberByName implements Comparator {
	public int compare(Object o1, Object o2) {
	    String s1 = ((Member)o1).getName();
	    String s2 = ((Member)o2).getName();

	    if (o1 instanceof Method) {
		s1 += getSignature((Method)o1);
		s2 += getSignature((Method)o2);
	    } else if (o1 instanceof Constructor) {
		s1 += getSignature((Constructor)o1);
		s2 += getSignature((Constructor)o2);
	    }
	    return s1.compareTo(s2);
	}
    }

    /* It is expensive to recompute a method or constructor signature
       many times, so compute it only once using this data structure. */
    private static class MethodSignature implements Comparator {
	Member member;
	String signature;      // cached parameter signature

	/* Given an array of Method or Constructor members,
	   return a sorted array of the non-private members.*/
	/* A better implementation would be to implement the returned data
	   structure as an insertion sorted link list.*/
	static MethodSignature[] removePrivateAndSort(Member[] m) {
	    int numNonPrivate = 0;
	    for (int i = 0; i < m.length; i++) {
		if (! Modifier.isPrivate(m[i].getModifiers())) {
		    numNonPrivate++;
		}
	    }
	    MethodSignature[] cm = new MethodSignature[numNonPrivate];
	    int cmi = 0;
	    for (int i = 0; i < m.length; i++) {
		if (! Modifier.isPrivate(m[i].getModifiers())) {
		    cm[cmi] = new MethodSignature(m[i]);
		    cmi++;
		}
	    }
	    if (cmi > 0)
		Arrays.sort(cm, cm[0]);
	    return cm;
	}

	/* Assumes that o1 and o2 are either both methods
	   or both constructors.*/
	public int compare(Object o1, Object o2) {
	    /* Arrays.sort calls compare when o1 and o2 are equal.*/
	    if (o1 == o2)
		return 0;
	    
	    MethodSignature c1 = (MethodSignature)o1;
	    MethodSignature c2 = (MethodSignature)o2;

	    int result;
	    if (isConstructor()) {
		result = c1.signature.compareTo(c2.signature);
	    } else { // is a Method.
		result = c1.member.getName().compareTo(c2.member.getName());
		if (result == 0)
		    result = c1.signature.compareTo(c2.signature);
	    }
	    return result;
	}

	private boolean isConstructor() {
	    return member instanceof Constructor;
	}

	private MethodSignature(Member m) {
	    member = m;
	    if (isConstructor()) {
		signature = ObjectStreamClass.getSignature((Constructor)m);
	    } else {
		signature = ObjectStreamClass.getSignature((Method)m);
	    }
	}
    }

    boolean isResolvable() {
	return readResolveMethod != null;
    }

    boolean isReplaceable() {
	return writeReplaceMethod != null;
    }

    static Object invokeMethod(Method method, Object obj, Object[] args)
	throws IOException
    {
	Object returnValue = null;
	try {
	    returnValue = method.invoke(obj, args);
	} catch (java.lang.reflect.InvocationTargetException e) {
	    Throwable t = e.getTargetException();
	    if (t instanceof IOException)
		throw (IOException)t;
	    else if (t instanceof RuntimeException)
		throw (RuntimeException) t;
	    else if (t instanceof Error)
		throw (Error) t;
	    else
		throw new Error("interal error");
	} catch (IllegalAccessException e) {
	    // cannot happen
	    throw new Error("interal error");
	}
	return returnValue;
    }

    /* ASSUMPTION: Called within priviledged access block. 
     *             Needed to set declared methods and to set the
     *             accessibility bit.
     */
    private Method getDeclaredMethod(String methodName, Class[] args, 
				     int requiredModifierMask,
				     int disallowedModifierMask) {
	Method method = null;
	try {
	    method = 
		ofClass.getDeclaredMethod(methodName, args);
	    if (method != null) {
		int mods = method.getModifiers();
		if ((mods & disallowedModifierMask) != 0 ||
		    (mods & requiredModifierMask) != requiredModifierMask) {
		    method = null;
		} else {
		    method.setAccessible(true);
		}
	    }
	} catch (NoSuchMethodException e) {
	    // Since it is alright if methodName does not exist,
	    // no need to do anything special here.
	}
	return method;
    }

    /*
     * Return true if scMethod is accessible from the context of 
     * this ObjectStreamClass' local implementation class.
     * Simulate java accessibility rules of accessing method 'scMethod' 
     * from a method within subclass this.forClass.
     * If method would not be accessible, returns null. 
     *
     * @param scMethod  A method from the superclass of this ObjectStreamClass.
     */
    private boolean checkSuperMethodAccess(Method scMethod) {
	if (scMethod == null) {
	    return false;
	}
	
	int supermods =  scMethod.getModifiers();
	if (Modifier.isPublic(supermods) || Modifier.isProtected(supermods)) {
	    return true;
	} else if (Modifier.isPrivate(supermods)) {
	    return false;
	} else {
	    // check package-private access.
	    return isSameClassPackage(scMethod.getDeclaringClass(), ofClass);
	}
    }

    /* Will not work for array classes. */
    static private boolean isSameClassPackage(Class cl1, Class cl2) {
	if (cl1.getClassLoader() != cl2.getClassLoader()) {
	    return false;
	} else {
	    String clName1 = cl1.getName();
	    String clName2 = cl2.getName();
	    int idx1 = clName1.lastIndexOf('.');
	    int idx2 = clName2.lastIndexOf('.');
	    if (idx1 == -1 || idx2 == -1) {
		/* One of the two doesn't have a package. Only return true
		 * if the other one also does not have a package.
		 */
		return idx1 == idx2;
	    } else {
		return clName1.regionMatches(false, 0, 
					     clName2, 0, idx1 - 1);
	    }
	}
    }

    private final static Class[] NULL_ARGS = {};
    
    //WORKAROUND compiler bug with following code.
    //static final Class[] OIS_ARGS = {ObjectInpuStream.class};
    //static final Class[] OOS_ARGS = {ObjectOutpuStream.class};
    private static Class[] OIS_ARGS = null;
    private static Class[] OOS_ARGS = null;
    private static void initStaticMethodArgs() {
	OOS_ARGS = new Class[1];
	OOS_ARGS[0] = ObjectOutputStream.class;
	OIS_ARGS = new Class[1];
	OIS_ARGS[0] = ObjectInputStream.class;
    }
}
