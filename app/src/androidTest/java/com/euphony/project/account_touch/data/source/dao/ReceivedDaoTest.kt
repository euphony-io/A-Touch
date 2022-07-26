package com.euphony.project.account_touch.data.source.dao

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.euphony.project.account_touch.data.entity.Bank
import com.euphony.project.account_touch.data.entity.Received
import com.euphony.project.account_touch.data.entity.model.BankIcon
import com.euphony.project.account_touch.data.entity.model.ExternalPackage
import com.euphony.project.account_touch.data.entity.model.UserIcon
import com.euphony.project.account_touch.data.source.EuphonyDatabase
import com.google.common.truth.Truth.assertThat
import junit.framework.TestCase
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.util.*

@RunWith(AndroidJUnit4::class)
class ReceivedDaoTest :TestCase(){
    private lateinit var dao: ReceivedDao
    private lateinit var bankDao: BankDao
    private lateinit var db: EuphonyDatabase

    @Before
    public override fun setUp() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        db = Room.inMemoryDatabaseBuilder(
            appContext,
            EuphonyDatabase::class.java
        ).build()

        dao = db.getReceivedDao()
        bankDao = db.getBankDao()
    }

    @Test
    fun 받은_계좌_생성() = runBlocking {
        //given
        val bankId = bankDao.addBank(Bank(1L, "국민은행", BankIcon.KB, 12, ExternalPackage.KOOKMIN))
        val received = Received(1L, bankId, "도영이의 국민 계좌", "123123123123", "은빈", UserIcon.GHOST)

        //when
        val newId = dao.addReceived(received)

        //then
        assertThat(received.id).isEqualTo(newId)
    }

    @Test
    fun 받은_계좌_리스트_조회() = runBlocking {
        //given
        val bankId = bankDao.addBank(Bank(1L, "국민은행", BankIcon.KB, 12, ExternalPackage.KOOKMIN))
        val received1 = Received(1L, bankId,
            "도영이의 국민 계좌", "123123123123",
            "은빈", UserIcon.GHOST, Date(2020,12,12))
        val received2 = Received(2L, bankId,
            "도영이의 하나 계좌", "32132132123",
            "은빈", UserIcon.GHOST, Date(2021,12,12))
        val received3 = Received(3L, bankId,
            "도영이의 카카오 계좌", "34532153",
            "은빈", UserIcon.GHOST, Date(2022,12,12))

        dao.addReceived(received1)
        dao.addReceived(received2)
        dao.addReceived(received3)

        //when
        val receivedList = dao.getAll();

        //then
        assertThat(receivedList.first().id).isEqualTo(received3.id)
        assertThat(receivedList.last().id).isEqualTo(received1.id)
    }

    @Test
    fun 받은_계좌_상세_조회() = runBlocking {
        //given
        val bankId = bankDao.addBank(Bank(1L, "국민은행", BankIcon.KB, 12, ExternalPackage.KOOKMIN))
        val received = Received(1L, bankId, "도영이의 국민 계좌", "123123123123", "은빈", UserIcon.GHOST)
        dao.addReceived(received)

        //when
        val findReceived = dao.getReceivedById(received.id);

        //then
        assertThat(findReceived.id).isEqualTo(received.id)
    }

    @Test
    fun 받은_계좌_삭제() = runBlocking {
        //given
        val bankId = bankDao.addBank(Bank(1L, "국민은행", BankIcon.KB, 12, ExternalPackage.KOOKMIN))
        val received = Received(1L,  bankId,"도영이의 국민 계좌", "123123123123", "은빈", UserIcon.GHOST)
        dao.addReceived(received)

        //when
        dao.deleteReceived(received);
        val receivedList = dao.getAll();

        //then
        assertThat(receivedList).isEmpty()
    }


    @After
    @Throws(IOException::class)
    fun cleanup() {
        db.close()
    }

}