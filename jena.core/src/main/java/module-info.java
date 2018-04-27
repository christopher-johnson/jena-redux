module org.apache.jena.core {
    requires slf4j.api;
    requires org.apache.jena.base;
    requires org.apache.jena.iri;
    requires org.apache.servicemix.bundles.xerces;
    requires java.xml;
    exports org.apache.jena.core;
    exports org.apache.jena.core.datatypes;
    exports org.apache.jena.core.graph;
    exports org.apache.jena.core.util;
    exports org.apache.jena.core.rdf.model;
    exports org.apache.jena.core.shared;
    exports org.apache.jena.core.datatypes.xsd;
    exports org.apache.jena.core.sys;
    exports org.apache.jena.core.rdf.model.impl;
    exports org.apache.jena.core.vocabulary;
}