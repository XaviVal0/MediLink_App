package com.example.medilink.Fragmentos

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.medilink.Adaptadores.AdaptadorAnuncio
import com.example.medilink.Adaptadores.AdaptadorCategoria
import com.example.medilink.Constantes
import com.example.medilink.Modelo.ModeloAnuncio
import com.example.medilink.Modelo.ModeloCategoria
import com.example.medilink.RvListenerCategoria
import com.example.medilink.databinding.FragmentInicioBinding
import com.google.firebase.database.*

class FragmentInicio : Fragment() {

    private var _binding: FragmentInicioBinding? = null
    private val binding get() = _binding!!
    private lateinit var mContext: Context

    private var anuncioArrayList: ArrayList<ModeloAnuncio> = ArrayList()
    private lateinit var adaptadorAnuncio: AdaptadorAnuncio
    private lateinit var adaptadorCategoria: AdaptadorCategoria
    private lateinit var anuncioListener: ValueEventListener
    private val ref = FirebaseDatabase.getInstance().getReference("Anuncios")

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInicioBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Configurar RecyclerView para anuncios
        binding.AnuncioView.setHasFixedSize(true)
        binding.AnuncioView.layoutManager = GridLayoutManager(requireContext(), 2)
        adaptadorAnuncio = AdaptadorAnuncio(requireContext(), anuncioArrayList)
        binding.AnuncioView.adapter = adaptadorAnuncio

        // Configurar RecyclerView para categorías
        binding.rvCategorias.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        // Cargar categorías y anuncios
        cargarCategorias()
        cargarAnuncios("Todos")

        // Implementar búsqueda
        binding.EtBuscar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adaptadorAnuncio.filter.filter(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // Botón para mostrar u ocultar categorías
        binding.btnMostrarCategorias.setOnClickListener {
            binding.rvCategorias.visibility = if (binding.rvCategorias.visibility == View.VISIBLE) View.GONE else View.VISIBLE
        }
    }

    private fun cargarCategorias() {
        val categoriaArrayList = ArrayList<ModeloCategoria>()
        for (categoria in Constantes.categorias) {
            categoriaArrayList.add(ModeloCategoria(categoria))
        }

        adaptadorCategoria = AdaptadorCategoria(
            requireContext(),
            categoriaArrayList,
            object : RvListenerCategoria {
                override fun onCategoriaClick(modeloCategoria: ModeloCategoria) {
                    cargarAnuncios(modeloCategoria.categoria)
                }
            }
        )
        binding.rvCategorias.adapter = adaptadorCategoria
    }

    private fun cargarAnuncios(categoria: String) {
        anuncioListener = ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val tempAnuncioList = ArrayList<ModeloAnuncio>()
                for (ds in snapshot.children) {
                    val modeloAnuncio = ds.getValue(ModeloAnuncio::class.java)
                    if (modeloAnuncio != null && (categoria == "Todos" || modeloAnuncio.categoria.equals(categoria, ignoreCase = true))) {
                        tempAnuncioList.add(modeloAnuncio)
                    }
                }
                anuncioArrayList.clear()
                anuncioArrayList.addAll(tempAnuncioList)
                adaptadorAnuncio.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                println("❌ Error al cargar anuncios: ${error.message}")
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        ref.removeEventListener(anuncioListener)
        _binding = null
    }
}