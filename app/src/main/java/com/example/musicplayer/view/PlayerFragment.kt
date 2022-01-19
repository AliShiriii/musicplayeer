package com.example.musicplayer.view

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.graphics.PorterDuff
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import com.example.musicplayer.R
import com.example.musicplayer.databinding.FragmentPlayerBinding
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.lang.Thread.sleep

@AndroidEntryPoint
class PlayerFragment : Fragment() {

    private var _binding: FragmentPlayerBinding? = null
    private val binding get() = _binding!!

    lateinit var songName: String
    var position: Int = 0
    private var mySongs: ArrayList<File> = ArrayList()
    lateinit var mediaPlayer: MediaPlayer

    lateinit var updateSeekBar: Thread

    companion object {
        val handler = Handler()
        const val EXTRA_NAME = "song_name"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return super.onOptionsItemSelected(item)

        if (item.itemId == android.R.id.home){

        }

    }
    private lateinit var args: PlayerFragmentArgs

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        _binding = FragmentPlayerBinding.inflate(inflater, container, false)

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkMediaPlayer()
    }

    private fun checkMediaPlayer() {

        if (mediaPlayer != null) {

            mediaPlayer.stop()
            mediaPlayer.release()

            mySongs = args.songsFile as ArrayList<File>
            position = args.position.toInt()

            binding.textSongs.isSelected = true
            binding.textSongs.text = args.songsName

            val url = Uri.parse(mySongs[position].toString())
            songName = mySongs[position].name

            binding.textSongs.text = songName

            mediaPlayer = MediaPlayer.create(requireActivity(), url)
            mediaPlayer.start()

            updateSeekBar = Thread(object : Runnable {
                override fun run() {

                    val totalDirection = mediaPlayer.duration
                    var currentPosition = 0

                    while (currentPosition < totalDirection) {

                        try {

                            sleep(500)
                            currentPosition = mediaPlayer.currentPosition
                            binding.songSeekBar.progress = currentPosition

                        } catch (e: IllegalStateException) {

                            e.printStackTrace()
                        }
                    }

                }


            })

            binding.songSeekBar.max = mediaPlayer.duration
            updateSeekBar.start()

            binding.songSeekBar.progressDrawable.setColorFilter(
                resources.getColor(R.color.design_default_color_primary),
                PorterDuff.Mode.MULTIPLY
            )
            binding.songSeekBar.thumb.setColorFilter(
                resources.getColor(R.color.design_default_color_primary),
                PorterDuff.Mode.MULTIPLY
            )

            binding.songSeekBar.setOnSeekBarChangeListener(object :
                SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                    TODO("Not yet implemented")
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {
                    TODO("Not yet implemented")
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {

                    mediaPlayer.seekTo(seekBar!!.progress)

                }

            })

            val endTime = currentTime(mediaPlayer.duration)

            binding.textSongs.text = endTime


            val delay = 1000

            handler.postDelayed(object : Runnable {
                override fun run() {

                    val currentTime = currentTime(mediaPlayer.currentPosition)

                    binding.textStart.text = currentTime

                }


            }, delay.toLong())

            binding.playButton.setOnClickListener {

                if (mediaPlayer.isPlaying) {

                    binding.playButton.setBackgroundResource(
                        R.drawable.ic_play
                    )

                    mediaPlayer.pause()

                } else {

                    binding.playButton.setBackgroundResource(
                        R.drawable.ic_pause
                    )

                    mediaPlayer.start()
                }

            }

        }


        val audios = mediaPlayer.audioSessionId

        if (audios != -1){

            binding.blast.setAudioSessionId(audios)
        }

        mediaPlayer.setOnCompletionListener {

            binding.nextButton.performClick()


        }

        binding.nextButton.setOnClickListener {

            mediaPlayer.stop()
            mediaPlayer.release()
            position = ((position + 1) % mySongs.size)

            val uri = Uri.parse(mySongs[position].toString())
            mediaPlayer = MediaPlayer.create(requireActivity(), uri)

            songName = mySongs[position].name
            binding.textSongs.text = songName
            mediaPlayer.start()

            binding.playButton.setBackgroundResource(R.drawable.ic_pause)
            startAnimation(binding.imageSongs)

            val audios = mediaPlayer.audioSessionId

            if (audios != -1){

                binding.blast.setAudioSessionId(audios)
            }

        }

        binding.backButton.setOnClickListener {

            mediaPlayer.stop()
            mediaPlayer.release()

//            position = ((position-1)<0)?(mySongs.size()-1):(position-1)

            val uri = Uri.parse(mySongs[position].toString())

            mediaPlayer = MediaPlayer.create(requireContext(), uri)
            songName = mySongs[position].name
            binding.textSongs.text = songName
            mediaPlayer.start()
            binding.playButton.setBackgroundResource(R.drawable.ic_pause)
            startAnimation(binding.imageSongs)

            val audios = mediaPlayer.audioSessionId

            if (audios != -1){

                binding.blast.setAudioSessionId(audios)
            }

        }

        binding.btnff.setOnClickListener {

            if (mediaPlayer.isPlaying) {

                mediaPlayer.seekTo(mediaPlayer.currentPosition + 10000)

            }
        }

        binding.btnfr.setOnClickListener {

            if (mediaPlayer.isPlaying) {

                mediaPlayer.seekTo(mediaPlayer.currentPosition - 10000)

            }

        }
    }


    private fun startAnimation(view: View) {

        val animator = ObjectAnimator.ofFloat(binding.imageSongs, "rotation", 0f, 360f)
        animator.duration = 1000

        val setAnimator = AnimatorSet()
        setAnimator.playTogether(animator)
        setAnimator.start()


    }

    fun currentTime(duration: Int): String {

        var time = ""
        val min = duration / 1000 / 60
        val sec = duration / 1000 % 60

        time += "$min:"

        if (sec > 10) {

            time += "0"

        }

        time += sec

        return time
    }
}