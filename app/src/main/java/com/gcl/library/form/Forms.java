package com.gcl.library.form;

import com.gcl.library.form.builder.ChatFormBuilder;
import com.gcl.library.form.builder.LoginFormBuilder;

public class Forms {

    public static LoginFormBuilder login() {
        return new LoginFormBuilder();
    }

    public static ChatFormBuilder chat() {
        return new ChatFormBuilder();
    }
}
