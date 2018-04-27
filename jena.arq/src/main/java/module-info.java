module org.apache.jena.arq {
    requires org.apache.jena.core;
    requires org.apache.jena.base;
    requires org.apache.jena.iri;
    requires slf4j.api;
    requires libthrift;
    requires httpcore;
    requires jsonld.java;
    requires jackson.core;
    requires jackson.databind;
    exports org.apache.jena.arq.system;
    exports org.apache.jena.arq.web;
    exports org.apache.jena.arq.sparql;
    exports org.apache.jena.arq.riot;
    exports org.apache.jena.arq.query;
    exports org.apache.jena.arq.atlas.csv;
    exports org.apache.jena.arq.atlas.data;
    exports org.apache.jena.arq.atlas.event;
    exports org.apache.jena.arq.atlas.json;
    exports org.apache.jena.arq.atlas.test;
    exports org.apache.jena.arq.atlas.web;
}