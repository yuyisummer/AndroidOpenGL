package com.knight.alphavideoplayer.giftvideo.player


abstract class VideoPlayerListener {
    open fun onCompletion(player: IPlayer) {

    }

    open fun onError(player: IPlayer, message: String) {

    }

    open fun onPrepared(player: IPlayer) {
        player.start()
    }

    open fun onVideoSizeChanged(player: IPlayer, width: Int, height: Int) {

    }
}