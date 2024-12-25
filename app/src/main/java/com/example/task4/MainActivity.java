package com.example.task4;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText inputRadius = findViewById(R.id.inputRadius);
        Button calculateButton = findViewById(R.id.calculateButton);

        calculateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String radiusStr = inputRadius.getText().toString();
                if (radiusStr.isEmpty()) {
                    Toast.makeText(MainActivity.this, getString(R.string.enter_radius), Toast.LENGTH_SHORT).show();
                    return;
                }

                double radius = Double.parseDouble(radiusStr);

                new Thread(() -> {
                    try {
                        URL url = new URL(getString(R.string.server_url));
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestMethod("POST");
                        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                        connection.setDoOutput(true);

                        OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
                        writer.write("radius=" + radius);
                        writer.flush();
                        writer.close();

                        int responseCode = connection.getResponseCode();
                        if (responseCode == 200) {
                            Scanner scanner = new Scanner(connection.getInputStream());
                            StringBuilder response = new StringBuilder();
                            while (scanner.hasNext()) {
                                response.append(scanner.nextLine());
                            }
                            scanner.close();

                            JSONObject jsonResponse = new JSONObject(response.toString());
                            if (jsonResponse.has("error")) {
                                String errorMessage = jsonResponse.getString("error");
                                runOnUiThread(() ->
                                        Toast.makeText(MainActivity.this, getString(R.string.error_message, errorMessage), Toast.LENGTH_SHORT).show()
                                );
                            } else {
                                double diameter = jsonResponse.getDouble("diameter");
                                double circumference = jsonResponse.getDouble("circumference");
                                double area = jsonResponse.getDouble("area");

                                runOnUiThread(() -> showResultDialog(diameter, circumference, area));
                            }
                        } else {
                            runOnUiThread(() ->
                                    Toast.makeText(MainActivity.this, getString(R.string.server_error, responseCode), Toast.LENGTH_SHORT).show()
                            );
                        }
                        connection.disconnect();
                    } catch (Exception e) {
                        runOnUiThread(() ->
                                Toast.makeText(MainActivity.this, getString(R.string.exception_message, e.getMessage()), Toast.LENGTH_SHORT).show()
                        );
                    }
                }).start();
            }
        });
    }

    private void showResultDialog(double diameter, double circumference, double area) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.result_title);
        builder.setMessage(getString(R.string.result_message, diameter, circumference, area));
        builder.setPositiveButton(R.string.ok, (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }
}
