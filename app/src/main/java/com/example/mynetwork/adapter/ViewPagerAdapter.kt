package com.example.mynetwork.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.mynetwork.ui.EventsFragment
import com.example.mynetwork.ui.PostsFragment
import com.example.mynetwork.ui.WallFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
class ViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> return WallFragment()
            1 -> return EventsFragment()
            //2 -> TODO("Добавить фрагмент с опытом работы")
        }
        return PostsFragment()
    }
}