package com.example.musicplayer.view

import android.Manifest
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.musicplayer.adapter.CustomAdapter
import com.example.musicplayer.databinding.FragmentHomeBinding
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    companion object {
        var items: ArrayList<String> = ArrayList()
        var mySongs: ArrayList<File> = ArrayList()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        runtimePermission()

    }

    private fun runtimePermission() {

        Dexter.withContext(requireContext())
            .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO)
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(p0: MultiplePermissionsReport?) {

                    displaySongs()
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: MutableList<PermissionRequest>?,
                    permissionToken: PermissionToken?
                ) {

                    permissionToken!!.continuePermissionRequest()
                }


            }).check()

    }

    private fun findSong(file: File): List<File> {

        val arrayList: ArrayList<File> = ArrayList()
        val files = file.listFiles()

        files.forEach { songsFile ->

            if (songsFile.isDirectory && !songsFile.isHidden) {

                arrayList.addAll(findSong(songsFile))
            } else {

                if (songsFile.name.endsWith(".mp3") || songsFile.name.endsWith(".wav")) {

                    arrayList.add(songsFile)
                }
            }
        }

        return arrayList

    }


    private fun displaySongs() {


        mySongs = findSong(Environment.getExternalStorageDirectory()) as ArrayList<File>

        items = ArrayList<String>(mySongs.size)

        mySongs.forEachIndexed { index, file ->

            items[index] = mySongs[index].name.toString().replace(".mp3", "")

        }

        setUpListView()
    }

    private fun setUpListView() {
        val songsAdapter = CustomAdapter()

        binding.recyclerViewSong.adapter = songsAdapter
        setOnClickListenerOnListView()

    }

    private fun setOnClickListenerOnListView() {

        binding.recyclerViewSong.setOnItemClickListener { adapterView, view, position, l ->

            val songName = binding.recyclerViewSong.getItemAtPosition(position) as String

            val action = HomeFragmentDirections.actionHomeFragmentToPlayerFragment(
                mySongs.toString(),
                songName,
                position.toString()
            )
            findNavController().navigate(action)

        }

    }

}










