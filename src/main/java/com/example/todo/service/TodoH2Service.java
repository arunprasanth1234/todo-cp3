/*
 * You can use the following import statements
 *
 * import org.springframework.beans.factory.annotation.Autowired;
 * import org.springframework.http.HttpStatus;
 * import org.springframework.jdbc.core.JdbcTemplate;
 * import org.springframework.stereotype.Service;
 * import org.springframework.web.server.ResponseStatusException;
 * import java.util.*;
 *
 */

// Write your code here

package com.example.todo.service;

import com.example.todo.model.Todo;

import com.example.todo.repository.TodoRepository;
import com.example.todo.model.TodoRowMapper;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.*;

@Service
public class TodoH2Service implements TodoRepository {
    @Autowired
    private JdbcTemplate db;

    @Override
    public ArrayList<Todo> getTodos() {
        List<Todo> todoList = db.query("select * from todolist", new TodoRowMapper());
        ArrayList<Todo> todos = new ArrayList<>(todoList);
        return todos;
    }

    @Override
    public Todo getTodoById(int todoId) {
        try {
            Todo todo = db.queryForObject("select * from todolist where todoId=?", new TodoRowMapper(), todoId);
            return todo;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public Todo addTodo(Todo todo) {
        db.update("insert into todolist(todo, status, priority) Values(?, ?, ?)",
                todo.getTodo(), todo.getStatus(), todo.getPriority());
        Todo savedTodo = db.queryForObject("select * from todolist where todo=? and status=?",
                new TodoRowMapper(), todo.getTodo(), todo.getStatus());
        return savedTodo;
    }

    @Override
    public Todo updateTodo(int id, Todo todo) {
        if (todo.getTodo() != null) {
            db.update("update todolist set todo=? where id=?", todo.getTodo(), id);
        }
        if (todo.getStatus() != null) {
            db.update("update todolist set status=? where id=?", todo.getStatus(), id);
        }
        if (todo.getPriority() != null) {
            db.update("update todolist set priority=? where id=?", todo.getPriority(), id);
        }
        return getTodoById(id);
    }

    @Override
    public void deleteTodo(int id) {
        db.update("delete from todolist where id=?", id);
    }
}
