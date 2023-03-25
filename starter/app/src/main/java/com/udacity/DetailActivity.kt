package com.udacity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.udacity.databinding.ContentDetailBinding
import com.udacity.utils.NameAndStatus

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ContentDetailBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ContentDetailBinding.inflate(layoutInflater)

        val nameAndStatus = intent.getParcelableExtra<NameAndStatus>("nameAndStatus")

        binding.nameAndStatus = nameAndStatus
        setContentView(binding.root)

        //Setting color of the status
        if(nameAndStatus?.status  == "FAILED"){
            binding.statusTextValue.setTextColor(Color.RED)
        }
        else{
            binding.statusTextValue.setTextColor(Color.GREEN)
        }

        //Getting back to Main Activity Via an Intent
        binding.okButton.setOnClickListener{
            val intentToMain = Intent(this, MainActivity::class.java)
            startActivity(intentToMain)
            finish()
        }
    }


}
