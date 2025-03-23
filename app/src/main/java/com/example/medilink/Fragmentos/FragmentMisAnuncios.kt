package com.example.medilink.Fragmentos

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.medilink.databinding.FragmentMisAnunciosBinding

class FragmentMisAnuncios : Fragment() {

    private lateinit var binding: FragmentMisAnunciosBinding
    private lateinit var mContext: Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.mContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflar el layout para este fragmento
        binding = FragmentMisAnunciosBinding.inflate(inflater, container, false)
        return binding.root
    }
}
