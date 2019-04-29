package ramirez.eduardo.logindemo

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
   var mAuth: FirebaseAuth? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mAuth = FirebaseAuth.getInstance();
        btnRegistrar.setOnClickListener(){
            registro()
        }
    }
    fun registro(){
        var correo = user.text.toString()
        var contra = pass.text.toString()
        mAuth?.createUserWithEmailAndPassword(correo, contra)
            ?.addOnCompleteListener(){task ->
                if(task.isSuccessful){
                    Toast.makeText(this,"Usuario Agregado", Toast.LENGTH_SHORT)

                }
                else
                    Toast.makeText(this,"ERROR: No se agregó", Toast.LENGTH_SHORT)

            }
    }
}
