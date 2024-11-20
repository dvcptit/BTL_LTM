module com.example.demojavafx {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    // requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires java.sql;
    requires static lombok;
    requires org.hibernate.orm.core;
    requires java.persistence;
    requires java.naming;
    requires java.validation;
    requires com.google.gson;
    requires jbcrypt;
    requires com.fasterxml.jackson.annotation;

    opens com.n19.ltmproject to javafx.fxml;
    exports com.n19.ltmproject.client.controller;
    opens com.n19.ltmproject.client.controller to javafx.fxml;
    exports com.n19.ltmproject.client;
    opens com.n19.ltmproject.client to javafx.fxml;
    opens com.n19.ltmproject.server.model to javafx.base, org.hibernate.orm.core, com.google.gson;
    opens com.n19.ltmproject.server.model.dto;
    exports com.n19.ltmproject.server.model.dto;
    exports com.n19.ltmproject.server.model;
    exports com.n19.ltmproject.client.test_request.game;
    opens com.n19.ltmproject.client.test_request.game to javafx.fxml;
    opens com.n19.ltmproject.client.model.dto to com.google.gson, javafx.base;
    opens com.n19.ltmproject.server.model.enums to com.google.gson;
    exports com.n19.ltmproject.server.model.enums to com.google.gson;
    opens com.n19.ltmproject.client.model to com.google.gson, java.base, javafx.base;
    exports com.n19.ltmproject.client.model to com.google.gson, java.base;
    exports com.n19.ltmproject.client.model.enums to com.google.gson, java.base;
    exports com.n19.ltmproject.client.test_request.game.test_logicPage;
    opens com.n19.ltmproject.client.test_request.game.test_logicPage to javafx.fxml;
}