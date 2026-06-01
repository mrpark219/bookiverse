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
import org.mockito.BDDMockito.then
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.http.MediaType
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
    @DisplayName("도서 대출 요청을 유스케이스로 전달하고 대출 항목을 응답한다")
    fun rentBook() {
        val command = RentBookCommand(
            userId = 1L,
            bookId = 10L,
            bookTitle = "오브젝트",
        )
        val rentalItem = Rental.create(userId = 1L).rentBook(
            bookId = 10L,
            bookTitle = "오브젝트",
        )
        given(rentBookUseCase.rentBook(command)).willReturn(rentalItem)

        mockMvc.perform(
            post("/rentals/{userId}/items", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "bookId": 10,
                      "bookTitle": "오브젝트"
                    }
                    """.trimIndent(),
                ),
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.bookId").value(10))
            .andExpect(jsonPath("$.bookTitle").value("오브젝트"))
            .andExpect(jsonPath("$.status").value(RentalItemStatus.RENTED.name))
            .andExpect(jsonPath("$.rentedDate").value(rentalItem.rentedDate.toString()))
            .andExpect(jsonPath("$.dueDate").value(rentalItem.dueDate.toString()))

        then(rentBookUseCase).should().rentBook(command)
    }

    @Test
    @DisplayName("도서 반납 요청을 유스케이스로 전달하고 반납 항목을 응답한다")
    fun returnBook() {
        val command = ReturnBookCommand(
            userId = 1L,
            bookId = 10L,
        )
        val rental = Rental.create(userId = 1L)
        val rentalItem = rental.rentBook(
            bookId = 10L,
            bookTitle = "오브젝트",
        )
        rental.returnBook(bookId = 10L)
        given(returnBookUseCase.returnBook(command)).willReturn(rentalItem)

        mockMvc.perform(
            post("/rentals/{userId}/items/{bookId}/return", 1L, 10L),
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.bookId").value(10))
            .andExpect(jsonPath("$.bookTitle").value("오브젝트"))
            .andExpect(jsonPath("$.status").value(RentalItemStatus.RETURNED.name))
            .andExpect(jsonPath("$.returnedDate").value(rentalItem.returnedDate.toString()))

        then(returnBookUseCase).should().returnBook(command)
    }
}
