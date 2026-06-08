package me.park.book.adapter.`in`.web

import me.park.book.application.query.GetBookQuery
import me.park.book.application.port.`in`.GetBookUseCase
import me.park.book.domain.Book
import me.park.book.domain.BookNotFoundException
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(BookController::class)
class BookControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var getBookUseCase: GetBookUseCase

    @Test
    @DisplayName("도서 ID로 도서 정보를 조회한다")
    fun getBookById() {
        // given
        val query = GetBookQuery(bookId = 10L)
        val book = Book(
            title = "오브젝트",
            author = "조영호",
            description = "객체지향 설계에 관한 책",
        )
        book.id = 10L
        given(getBookUseCase.getBook(query)).willReturn(book)

        // when
        val resultActions = mockMvc.perform(
            get("/books/{bookId}", 10L),
        )

        // then
        resultActions
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(10))
            .andExpect(jsonPath("$.title").value("오브젝트"))
            .andExpect(jsonPath("$.author").value("조영호"))
            .andExpect(jsonPath("$.description").value("객체지향 설계에 관한 책"))
    }

    @Test
    @DisplayName("도서 정보가 없으면 404로 응답한다")
    fun getBookByIdNotFound() {
        // given
        val query = GetBookQuery(bookId = 10L)
        given(getBookUseCase.getBook(query))
            .willThrow(BookNotFoundException("도서 정보를 찾을 수 없습니다. bookId=10"))

        // when
        val resultActions = mockMvc.perform(
            get("/books/{bookId}", 10L),
        )

        // then
        resultActions
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.message").value("도서 정보를 찾을 수 없습니다. bookId=10"))
    }
}
