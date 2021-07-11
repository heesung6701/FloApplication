package com.quokkaman.floapplication.repository

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsNull
import org.junit.Test
import java.util.concurrent.CountDownLatch
import org.hamcrest.Matchers.`is` as Is

class SongRepositoryTest {

    private val songRepository = SongRepository

    private fun timeToLong(m: Int, s: Int, ms: Int): Long = (m * 60 + s).toLong() * 1000 + ms

    @Test
    fun getSongText() {
        val signal = CountDownLatch(1)
        songRepository.getSong()
            .subscribeOn(Schedulers.newThread())
//            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                val song = it.toModel()
                assertThat(song.singer, Is("챔버오케스트라"))
                assertThat(song.album, Is("캐롤 모음"))
                assertThat(song.title, Is("We Wish You A Merry Christmas"))
                assertThat(song.duration, Is(198))
                assertThat(
                    song.imageUrl,
                    Is("https://grepp-programmers-challenges.s3.ap-northeast-2.amazonaws.com/2020-flo/cover.jpg")
                )
                assertThat(
                    song.fileUrl,
                    Is("https://grepp-programmers-challenges.s3.ap-northeast-2.amazonaws.com/2020-flo/music.mp3")
                )
                val list = arrayListOf(
                    Pair(timeToLong(0, 16, 200), "we wish you a merry christmas"),
                    Pair(timeToLong(0, 18, 300), "we wish you a merry christmas"),
                    Pair(timeToLong(0, 21, 100), "we wish you a merry christmas"),
                    Pair(timeToLong(0, 23, 600), "and a happy new year"),
                    Pair(timeToLong(0, 26, 300), "we wish you a merry christmas"),
                    Pair(timeToLong(0, 28, 700), "we wish you a merry christmas"),
                    Pair(timeToLong(0, 31, 400), "we wish you a merry christmas"),
                    Pair(timeToLong(0, 33, 600), "and a happy new year"),
                    Pair(timeToLong(0, 36, 500), "good tidings we bring"),
                    Pair(timeToLong(0, 38, 900), "to you and your kin"),
                    Pair(timeToLong(0, 41, 500), "good tidings for christmas"),
                    Pair(timeToLong(0, 44, 200), "and a happy new year"),
                    Pair(timeToLong(0, 46, 600), "Oh, bring us some figgy pudding"),
                    Pair(timeToLong(0, 49, 300), "Oh, bring us some figgy pudding"),
                    Pair(timeToLong(0, 52, 200), "Oh, bring us some figgy pudding"),
                    Pair(timeToLong(0, 54, 500), "And bring it right here"),
                    Pair(timeToLong(0, 57, 0), "Good tidings we bring "),
                    Pair(timeToLong(0, 59, 700), "to you and your kin"),
                    Pair(timeToLong(1, 2, 100), "Good tidings for Christmas "),
                    Pair(timeToLong(1, 4, 800), "and a happy new year"),
                    Pair(timeToLong(1, 7, 400), "we wish you a merry christmas"),
                    Pair(timeToLong(1, 10, 0), "we wish you a merry christmas"),
                    Pair(timeToLong(1, 12, 500), "we wish you a merry christmas"),
                    Pair(timeToLong(1, 15, 0), "and a happy new year"),
                    Pair(timeToLong(1, 17, 700), "We won't go until we get some"),
                    Pair(timeToLong(1, 20, 200), "We won't go until we get some"),
                    Pair(timeToLong(1, 22, 800), "We won't go until we get some"),
                    Pair(timeToLong(1, 25, 300), "So bring some out here"),
                    Pair(timeToLong(1, 29, 800), "연주"),
                    Pair(timeToLong(2, 11, 900), "Good tidings we bring "),
                    Pair(timeToLong(2, 14, 0), "to you and your kin"),
                    Pair(timeToLong(2, 16, 500), "good tidings for christmas"),
                    Pair(timeToLong(2, 19, 400), "and a happy new year"),
                    Pair(timeToLong(2, 22, 0), "we wish you a merry christmas"),
                    Pair(timeToLong(2, 24, 400), "we wish you a merry christmas"),
                    Pair(timeToLong(2, 27, 0), "we wish you a merry christmas"),
                    Pair(timeToLong(2, 29, 600), "and a happy new year"),
                    Pair(timeToLong(2, 32, 200), "Good tidings we bring "),
                    Pair(timeToLong(2, 34, 500), "to you and your kin"),
                    Pair(timeToLong(2, 37, 200), "Good tidings for Christmas "),
                    Pair(timeToLong(2, 40, 0), "and a happy new year"),
                    Pair(timeToLong(2, 42, 400), "Oh, bring us some figgy pudding"),
                    Pair(timeToLong(2, 45, 0), "Oh, bring us some figgy pudding"),
                    Pair(timeToLong(2, 47, 600), "Oh, bring us some figgy pudding"),
                    Pair(timeToLong(2, 50, 200), "And bring it right here"),
                    Pair(timeToLong(2, 52, 600), "we wish you a merry christmas"),
                    Pair(timeToLong(2, 55, 300), "we wish you a merry christmas"),
                    Pair(timeToLong(2, 57, 900), "we wish you a merry christmas"),
                    Pair(timeToLong(3, 0, 500), "and a happy new year")
                )
                song.lines.forEachIndexed { index, line ->
                    assertThat(line.time, Is(list[index].first))
                    assertThat(line.lyrics, Is(list[index].second))
                }
                signal.countDown()
            }, { err ->
                assertThat(err, IsNull())
                signal.countDown()
            })
        signal.await()
    }
}
