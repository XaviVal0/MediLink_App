package com.example.medilink.Fragmentos

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.medilink.R
import com.example.medilink.databinding.FragmentMisProductosBinding
import com.google.android.material.tabs.TabLayout

class FragmentMisProductos : Fragment() {

    private lateinit var binding: FragmentMisProductosBinding
    private lateinit var mContext: Context
    private lateinit var mTabsViewPagerAdapter: MyTabsViewPagerAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.mContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflar el layout para este fragmento
        binding = FragmentMisProductosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Configurar las pestañas
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Mis anuncios"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Favoritos"))

        // Configurar el adaptador del ViewPager2
        val fragmentManager = childFragmentManager
        mTabsViewPagerAdapter = MyTabsViewPagerAdapter(fragmentManager, lifecycle)
        binding.viewPager.adapter = mTabsViewPagerAdapter

        // Sincronizar el cambio de tab con el ViewPager
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                binding.viewPager.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                // No hace falta hacer nada aquí por ahora
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                // No hace falta hacer nada aquí por ahora
            }
        })

        // Sincronizar el cambio de página con el TabLayout
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                binding.tabLayout.selectTab(binding.tabLayout.getTabAt(position))
            }
        })
    }

    class MyTabsViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
        FragmentStateAdapter(fragmentManager, lifecycle) {

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> Mis_Anuncios_PublicadosFragment()
                else -> Fav_Anuncios_Fragment()
            }
        }

        override fun getItemCount(): Int = 2
    }
}
