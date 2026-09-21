package com.example.data

import kotlinx.coroutines.flow.Flow

class TicketRepository(private val ticketDao: TicketDao) {
    val allTickets: Flow<List<TicketEntity>> = ticketDao.getAllTickets()

    suspend fun insert(ticket: TicketEntity): Long = ticketDao.insertTicket(ticket)

    suspend fun update(ticket: TicketEntity) = ticketDao.updateTicket(ticket)

    suspend fun delete(ticket: TicketEntity) = ticketDao.deleteTicket(ticket)

    suspend fun deleteById(id: Int) = ticketDao.deleteTicketById(id)

    suspend fun deleteAll() = ticketDao.deleteAllTickets()
}
