package kr.com.misemung.ui.adapter

import android.util.Pair
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter
import java.util.*

class DustPagerAdapter(fragment: FragmentManager?, fragmentList: ArrayList<Pair<Fragment, String?>>) :
    FragmentStatePagerAdapter(fragment!!) {
    private val fragmentList: MutableList<Pair<Fragment, String?>>

    fun getItemCount(): Int {
        return fragmentList.size
    }

    fun createFragment(position: Int): Fragment {
        return fragmentList[position].first
    }

    // Delete a page at a `position`
    fun deletePage(position: Int) {
        // Remove the corresponding item in the data set
        fragmentList.removeAt(position)
        // Notify the adapter that the data set is changed
        notifyDataSetChanged()
    }

    init {
        this.fragmentList = fragmentList
    }

    override fun getCount(): Int {
        return fragmentList.size
    }

    override fun getItem(position: Int): Fragment {
        return fragmentList[position].first
    }

    override fun getItemPosition(obj: Any): Int {
        return POSITION_NONE
    }

}