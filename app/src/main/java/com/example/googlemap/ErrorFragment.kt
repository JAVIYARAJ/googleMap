package com.example.googlemap

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton

class ErrorFragment : Fragment() {

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_error, container, false)

        var allowPermissionBtn=view.findViewById<AppCompatButton>(R.id.allowPermissionBtn);

        allowPermissionBtn.setOnClickListener {
            var intent=Intent()
            intent.action=Settings.ACTION_APPLICATION_DETAILS_SETTINGS;
            intent.data= Uri.parse("package:"+requireActivity().packageName);
            startActivity(intent)
        }

        return view;
    }


}