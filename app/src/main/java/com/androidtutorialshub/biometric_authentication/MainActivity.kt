package com.androidtutorialshub.biometric_authentication

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import java.util.concurrent.Executor

class MainActivity : AppCompatActivity() {

    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo
    private lateinit var promptInfoWithPin: BiometricPrompt.PromptInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // initialize the biometric prompt
        initBiometricPrompt()

        // login button on click listener
        findViewById<Button>(R.id.buttonBiometricAuth).setOnClickListener {
            // check if biometric supported
            if (isBioMetricSupported()) {
                // call authenticate method to show prompt for biometric
                biometricPrompt.authenticate(promptInfo)
            } else {
                // call toast if biometric not supported
                Toast.makeText(this, "Biometric not supported", Toast.LENGTH_LONG).show()
            }
        }

        // login button on click listener
        findViewById<Button>(R.id.buttonBiometricAuthWithPin).setOnClickListener {
            // check if biometric supported
            if (isBioMetricSupported()) {
                // call authenticate method to show prompt for biometric + pin
                biometricPrompt.authenticate(promptInfoWithPin)
            } else {
                // call toast if biometric not supported
                Toast.makeText(this, "Biometric not supported", Toast.LENGTH_LONG).show()
            }
        }
    }

    /**
     * method to initialize the biometric prompt configuration
     */
    private fun initBiometricPrompt() {
        // executor to enqueued tasks on the main thread
        executor = ContextCompat.getMainExecutor(this)

        // biometricPrompt to show biometric pop up for authentication
        biometricPrompt = BiometricPrompt(
            this,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                // authentication error callback
                override fun onAuthenticationError(
                    errorCode: Int,
                    errString: CharSequence
                ) {
                    super.onAuthenticationError(errorCode, errString)
                    Toast.makeText(
                        applicationContext,
                        "Authentication error: $errString", Toast.LENGTH_SHORT
                    ).show()
                }

                // authentication success  callback
                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult
                ) {
                    super.onAuthenticationSucceeded(result)
                    Toast.makeText(
                        applicationContext,
                        "Authentication succeeded!", Toast.LENGTH_SHORT
                    ).show()
                }

                // authentication failure callback
                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(
                        applicationContext, "Authentication failed",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })

        // promptInfo to initialize the pop up title, subtitle and negative button text
        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Login to Android Tutorials Hub")
            .setSubtitle("Biometric Authentication")
            .setDescription("Use your fingerprint to access the app")
            .setNegativeButtonText("Cancel")
            .build()

        // promptInfo to initialize the pop up title, subtitle and pin password option enabled
        promptInfoWithPin = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Login to Android Tutorials Hub")
            .setSubtitle("Biometric Authentication + Pin Password")
            .setDescription("Use your fingerprint or pin to access the app")
            .setDeviceCredentialAllowed(true)
            .build()
    }

    /**
     * method to check biometric is supported
     *
     * return true/false
     */
    private fun isBioMetricSupported(): Boolean {

        // biometric manager
        val biometricManager = BiometricManager.from(this)

        // check if app can authenticate via biometric
        when (biometricManager.canAuthenticate()) {
            // success
            BiometricManager.BIOMETRIC_SUCCESS -> {
                Log.e("Biometric Status", "App can authenticate using biometrics.")
                return true
            }
            // error no hardware
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                Log.e("Biometric Status", "No biometric features available on this device.")
                return false
            }
            // error feature unavailable
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                Log.e("Biometric Status", "Biometric features are currently unavailable.")
                return false
            }
            // error no biometric enrolled
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                Log.e(
                    "Biometric Status",
                    "The user hasn't associated any biometric credentials with their account."
                )
                return false
            }
        }
        return false
    }

}