package com.n19.ltmproject.server.command;

import com.n19.ltmproject.server.model.dto.Request;
import com.n19.ltmproject.server.model.dto.Response;

/**
 * Interface for command pattern
 * Each implementation of this interface should be a specific command,
 * and it kinda like API for the client to interact with the server
 */
public interface Command {
    Response execute(Request request);
}
