package me.park.rental.adapter.`in`.web

import me.park.rental.application.command.RentBookCommand
import me.park.rental.application.command.ReturnBookCommand
import me.park.rental.application.port.`in`.RentBookUseCase
import me.park.rental.application.port.`in`.ReturnBookUseCase
import me.park.rental.domain.Rental
import me.park.rental.domain.RentalItemStatus
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(RentalController::class)
class RentalControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var rentBookUseCase: RentBookUseCase

    @MockitoBean
    private lateinit var returnBookUseCase: ReturnBookUseCase

    @Test
    @DisplayName("도서 대출 요청을 유스케이스로 전달하고 대기 중인 대출 항목을 응답한다")
    fun rentBook() {
        // given
        val command = RentBookCommand(
            userId = 1L,
            bookId = 10L,
        )
        val rentalItem = Rental.create(userId = 1L).rentBook(
            bookId = 10L,
            bookTitle = "오브젝트",
            stockDeductRequestId = "11111111-1111-1111-1111-111111111111",
        )
        rentalItem.id = 100L
        given(rentBookUseCase.rentBook(command)).willReturn(rentalItem)

        // when
        val resultActions = mockMvc.perform(
            post("/rentals/{userId}/books/{bookId}", 1L, 10L),
        )

        // then
        resultActions
            .andExpect(status().isAccepted)
            .andExpect(jsonPath("$.rentalItemId").value(100))
            .andExpect(jsonPath("$.stockDeductRequestId").value("11111111-1111-1111-1111-111111111111"))
            .andExpect(jsonPath("$.bookId").value(10))
            .andExpect(jsonPath("$.bookTitle").value("오브젝트"))
            .andExpect(jsonPath("$.status").value(RentalItemStatus.PENDING.name))
            .andExpect(jsonPath("$.rentedDate").value(rentalItem.rentedDate.toString()))
            .andExpect(jsonPath("$.dueDate").value(rentalItem.dueDate.toString()))
    }

    @Test
    @DisplayName("도서 반납 요청을 유스케이스로 전달하고 반납 항목을 응답한다")
    fun returnBook() {
        // given
        val command = ReturnBookCommand(
            userId = 1L,
            bookId = 10L,
        )
        val rental = Rental.create(userId = 1L)
        val rentalItem = rental.rentBook(
            bookId = 10L,
            bookTitle = "오브젝트",
            stockDeductRequestId = "11111111-1111-1111-1111-111111111111",
        )
        rentalItem.status = RentalItemStatus.RENTED
        rental.returnBook(bookId = 10L)
        given(returnBookUseCase.returnBook(command)).willReturn(rentalItem)

        // when
        val resultActions = mockMvc.perform(
            post("/rentals/{userId}/items/{bookId}/return", 1L, 10L),
        )

        // then
        resultActions
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.bookId").value(10))
            .andExpect(jsonPath("$.bookTitle").value("오브젝트"))
            .andExpect(jsonPath("$.status").value(RentalItemStatus.RETURNED.name))
            .andExpect(jsonPath("$.returnedDate").value(rentalItem.returnedDate.toString()))
    }
}
