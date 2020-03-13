package com.example.guuber;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class Maintest extends AppCompatActivity {

        // Declare the variables so that you will be able to reference it later.


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            GuuDb gdb = new GuuDb();
            gdb.setUpUser("123@123.ca","Luke","Atme","LukeAtme","780 780 7807","rider");
            gdb.findUser("123@123.ca");











        }

    }

}
