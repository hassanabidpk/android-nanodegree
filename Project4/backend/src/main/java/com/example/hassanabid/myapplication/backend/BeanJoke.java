package com.example.hassanabid.myapplication.backend;

import com.example.TellJokes;

/** The object model for the data we are sending through endpoints */
public class BeanJoke {

    private TellJokes tellJoke;

    public BeanJoke(){
        tellJoke = new TellJokes();

    }

    public String getTellJokes () {

        return  tellJoke.getJoke();
    }

}