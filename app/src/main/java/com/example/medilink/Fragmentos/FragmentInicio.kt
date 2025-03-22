package com.example.medilink.Fragmentos

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.example.medilink.Adaptadores.AdaptadorAnuncio
import com.example.medilink.Adaptadores.AdaptadorCategoria
import com.example.medilink.Constantes
import com.example.medilink.Filtro.FiltroAnuncio
import com.example.medilink.Modelo.ModeloAnuncio
import com.example.medilink.Modelo.ModeloCategoria
import com.example.medilink.RvListenerCategoria
import com.example.medilink.databinding.FragmentInicioBinding
import com.google.firebase.database.*

class FragmentInicio : Fragment() {

    private lateinit var binding: FragmentInicioBinding
    private lateinit var mContext: Context

    private var anuncioArrayList: ArrayList<ModeloAnuncio> = ArrayList()
    private lateinit var adaptadorAnuncio: AdaptadorAnuncio

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentInicioBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Configurar RecyclerView para anuncios
        binding.AnuncioView.setHasFixedSize(true)
        binding.AnuncioView.layoutManager = GridLayoutManager(mContext, 2)
        adaptadorAnuncio = AdaptadorAnuncio(mContext, anuncioArrayList)
        binding.AnuncioView.adapter = adaptadorAnuncio

        // Cargar categorías y anuncios
        cargarCategorias()
        cargarAnuncios("Todos")

        // Implementar búsqueda
        binding.EtBuscar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adaptadorAnuncio.filter.filter(s.toString()) // Aplicar filtro con el texto ingresado
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.IbClear.setOnClickListener {
            binding.EtBuscar.setText("")
            Toast.makeText(mContext, "Texto borrado", Toast.LENGTH_SHORT).show()
        }
    }

    private fun cargarCategorias() {
        val categoriaArrayList = ArrayList<ModeloCategoria>()
        for (i in Constantes.categorias.indices) {
            val modeloCategoria = ModeloCategoria(Constantes.categorias[i], Constantes.categoriaIconos[i])
            categoriaArrayList.add(modeloCategoria)
        }

        val adaptadorCategoria = AdaptadorCategoria(
            mContext,
            categoriaArrayList,
            object : RvListenerCategoria {
                override fun onCategoriaClick(modeloCategoria: ModeloCategoria) {
                    cargarAnuncios(modeloCategoria.categoria)
                }
            }
        )

        binding.CategoriaView.adapter = adaptadorCategoria
    }

    private fun cargarAnuncios(categoria: String) {
        val ref = FirebaseDatabase.getInstance().getReference("Anuncios")

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                anuncioArrayList.clear()

                for (ds in snapshot.children) {
                    try {
                        val modeloAnuncio = ds.getValue(ModeloAnuncio::class.java)

                        if (modeloAnuncio != null) {
                            if (categoria.equals("Todos", ignoreCase = true) ||
                                modeloAnuncio.categoria.equals(categoria, ignoreCase = true)) {
                                anuncioArrayList.add(modeloAnuncio)
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                adaptadorAnuncio.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                println("❌ Error al cargar anuncios: ${error.message}")
            }
        })
    }
}
