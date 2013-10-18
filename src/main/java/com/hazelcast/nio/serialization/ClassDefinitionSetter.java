package com.hazelcast.nio.serialization;

/**
 * It has to be in the com.hazelcast package due package protected-access on the classDefinition field in Data
 *
 */
public class ClassDefinitionSetter {

    public static void setClassDefinition(ClassDefinition classDefinition, Data data) {
        data.classDefinition = classDefinition;
    }
}
