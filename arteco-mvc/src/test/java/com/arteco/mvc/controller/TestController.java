package com.arteco.mvc.controller;

import com.arteco.mvc.annotation.RequestMethod;

/**
 * Created by amalagraba on 20/03/2017.
 * Arteco Consulting Sl
 * mailto: info@arteco-consulting.com
 */
@RequestMethod(value = "/path")
public class TestController extends TestControllerParent {

    @RequestMethod("method")
    public String testMethod() {
        return "test";
    }
}
