package com.erdince.yabancidilkelimehaznesi6.activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.erdince.yabancidilkelimehaznesi6.R
import kotlinx.android.synthetic.*


class emailVerifyFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }//saddsf

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root =  inflater.inflate(R.layout.fragment_email_verify, container, false)
        val verifyButton: Button = root.findViewById(R.id.button2)
        verifyButton.setOnClickListener(){
            Toast.makeText(view?.context, "tikladin",Toast.LENGTH_LONG).show()
        }
        return root

    }


}