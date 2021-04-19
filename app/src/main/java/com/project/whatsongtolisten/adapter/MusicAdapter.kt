package com.project.whatsongtolisten.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.project.whatsongtolisten.R
import com.project.whatsongtolisten.common.data.Song
import com.project.whatsongtolisten.utils.LocalData

class MusicAdapter (private var itemList: MutableList<Song>) : RecyclerView.Adapter<MusicAdapter.Holder>() {
    private lateinit var itemMovieListCallback: ItemMovieClickCallback

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_song, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(itemList[position])
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val poster: ImageView = itemView.findViewById(R.id.item_band_poster)
        val title: TextView = itemView.findViewById(R.id.title)
        val artist: TextView = itemView.findViewById(R.id.artist)
        val albumCollection: TextView = itemView.findViewById(R.id.album)
        val isNowPlaying: TextView = itemView.findViewById(R.id.ic_now_playing)
        val songItem: CardView = itemView.findViewById(R.id.cv_song_item)

        fun bind(song: Song) {
            title.text = song.trackName
            artist.text = song.artistName
            albumCollection.text = song.collectionName ?: "-"

            if (LocalData.getNowPlayingMusic() == song) {
                isNowPlaying.text = "Now Playing"
            } else {
                isNowPlaying.text = ""
            }

            Glide.with(itemView)
                    .load(song.artworkUrl100)
                    .transform(CenterCrop())
                    .placeholder(R.drawable.image)
                    .into(poster)
        }

        init {
            songItem.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            when(view.id) {
                R.id.cv_song_item -> {
                    itemMovieListCallback.onItemClick(adapterPosition)
                }
            }
        }
    }

    interface ItemMovieClickCallback {
        fun onItemClick(p: Int)
    }

    fun setItemClickCallback(itemMovieClickCallback: ItemMovieClickCallback) {
        this.itemMovieListCallback = itemMovieClickCallback
    }
}