package com.example.hackathonproj;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import java.util.Scanner;

public class User extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String username = "steelhacks";
        String paswrd = "WEWINTHEGAME!";
        int vaild_try = 2;

        Scanner scanner = new Scanner(System.in);

        //login or create a new account
        System.out.print("Do you have an account? Enter 'login' or 'create' a new account:");
        String output = scanner.nextLine();
        if (output.equals("create"))
        {
            System.out.print("Create your username: ");
            username = scanner.nextLine();
            System.out.print("Create your password: ");
            paswrd = scanner.nextLine();
        }

        //login to account
        System.out.println("Login Page:");
        System.out.print("Enter your username: ");
        String user_name = scanner.nextLine();
        System.out.print("Enter your password: ");
        String password = scanner.nextLine();

        //3 try for the user_name and the password
        for(int i = 0; i<vaild_try; i++)
        {
            if(!user_name.equals(username) || !password.equals(paswrd))
            {
                System.out.println("Your username or password is wrong, please try again");
                System.out.print("Enter your username: ");
                user_name = scanner.nextLine();
                System.out.print("Enter your password: ");
                password = scanner.nextLine();
                if(i==vaild_try-1 && (!user_name.equals(username) || !password.equals(paswrd))){
                    System.out.println("Your account has been locked!");
                }
            }
        }

        scanner.close();
    }
}