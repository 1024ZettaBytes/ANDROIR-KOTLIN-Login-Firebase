package com.ramirez.logingoogle

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.GoogleApiClient
import kotlinx.android.synthetic.main.activity_login.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import android.support.annotation.NonNull
import android.view.View
import com.google.android.gms.auth.api.signin.GoogleSignInAccount

import com.google.firebase.auth.AuthResult
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.AuthCredential


class LoginActivity : AppCompatActivity(), GoogleApiClient.OnConnectionFailedListener {

    lateinit private var firebaseAuth: FirebaseAuth
    lateinit private var firebaseAuthListener: FirebaseAuth.AuthStateListener
    override fun onConnectionFailed(p0: ConnectionResult) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    var clienteApiGoogle: GoogleApiClient? = null
    val CODIGO_SIG_IN = 777
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestProfile()
            .requestEmail()
            .requestIdToken(getString(R.string.default_web_client_id))
            .build()
        clienteApiGoogle = GoogleApiClient.Builder(this)
            .enableAutoManage(this, this)
            .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
            .build()
        sigInButton.setOnClickListener() {
            var intent: Intent = Auth.GoogleSignInApi.getSignInIntent(clienteApiGoogle)
            startActivityForResult(intent, CODIGO_SIG_IN)
        }
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuthListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                abrirPrincipal()
            }
        }

    }

    override fun onStart() {
        super.onStart()
        firebaseAuth.addAuthStateListener(firebaseAuthListener)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CODIGO_SIG_IN) {
            val resultado: GoogleSignInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            manejarResultadoSignin(resultado)
        }
    }

    private fun manejarResultadoSignin(resultado: GoogleSignInResult) {
        if (resultado.isSuccess) {
            autenticarFirebase(resultado.getSignInAccount());
        } else {
            Toast.makeText(this, "ERROR: No se pudo iniciar sesión.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun autenticarFirebase(signInAccount: GoogleSignInAccount?) {
        progresoLogin.visibility= View.VISIBLE
        sigInButton.visibility = View.GONE
        val credential = GoogleAuthProvider.getCredential(signInAccount?.idToken, null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(
            this
        ) { task ->
            progresoLogin.visibility= View.GONE
            sigInButton.visibility = View.VISIBLE
            if (!task.isSuccessful) {
                Toast.makeText(applicationContext, "ERROR: No se pudo autenticar con firebase.", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun abrirPrincipal() {
        val intent: Intent = Intent(this, MainActivity::class.java)
        intent.flags =
            Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    override fun onStop() {
        super.onStop()
        if (firebaseAuthListener != null) {
            firebaseAuth.removeAuthStateListener(firebaseAuthListener)
        }
    }
}
