package io.github.lmller.thesaurusrex.findsynonyms

import io.github.lmller.thesaurusrex.Synset
import io.github.lmller.thesaurusrex.Term
import io.reactivex.Observable
import io.reactivex.schedulers.TestScheduler
import io.reactivex.subjects.PublishSubject
import org.hamcrest.CoreMatchers.equalTo
import org.junit.Assert.assertThat
import org.junit.Test
import java.util.concurrent.TimeUnit

class SearchViewModelTest {

    val stubSynonyms = Thesaurus {
        val expectedTerms1 = listOf(Term("Erprobung"))
        val expectedTerms2 = listOf(Term("Kontrolle"), Term("Leistungsnachweis"))
        val sets = listOf(
                Synset(1L, terms = expectedTerms1),
                Synset(2L, terms = expectedTerms2)
        )
        Observable.fromIterable(sets)
    }


    @Test
    fun `Each search result term is in a new line`() {
        val testScheduler = TestScheduler()
        val textObserver = PublishSubject.create<String>()

        val viewModel = SearchViewModel(stubSynonyms, textObserver, testScheduler)

        textObserver.onNext("Test")
        testScheduler.advanceTimeBy(500, TimeUnit.MILLISECONDS)

        assertThat(viewModel.searchResults, equalTo("Erprobung\nKontrolle\nLeistungsnachweis"))
    }

    @Test
    fun `Search results are cleared every time a new text is entered`() {
        val testScheduler = TestScheduler()
        val textObserver = PublishSubject.create<String>()

        val viewModel = SearchViewModel(stubSynonyms, textObserver, testScheduler)

        textObserver.onNext("Test")
        testScheduler.advanceTimeBy(500, TimeUnit.MILLISECONDS)

        textObserver.onNext("Test1")
        testScheduler.advanceTimeBy(500, TimeUnit.MILLISECONDS)

        textObserver.onNext("Test2")
        testScheduler.advanceTimeBy(500, TimeUnit.MILLISECONDS)

        assertThat(viewModel.searchResults, equalTo("Erprobung\nKontrolle\nLeistungsnachweis"))
    }

    @Test
    fun `A progress bar is visible after the request was started`() {
        val testScheduler = TestScheduler()
        val textObserver = PublishSubject.create<String>()
        val viewModel = SearchViewModel(Thesaurus { Observable.never() }, textObserver, testScheduler)

        textObserver.onNext("Test")
        testScheduler.advanceTimeBy(500, TimeUnit.MILLISECONDS)

        assertThat(viewModel.isProgressBarVisible, equalTo(true))
    }

    @Test
    fun `The progress bar is invisible after the request has finished`() {
        val textObserver = PublishSubject.create<String>()
        val viewModel = SearchViewModel(Thesaurus { Observable.empty() }, textObserver)

        textObserver.onNext("Test")

        assertThat(viewModel.isProgressBarVisible, equalTo(false))
    }

    @Test
    fun `An error message is visible if the api call resulted in an error`(){
        val testScheduler = TestScheduler()
        val textObserver = PublishSubject.create<String>()
        val viewModel = SearchViewModel(Thesaurus { Observable.error(Exception("Sorry")) }, textObserver, testScheduler)

        textObserver.onNext("Test")
        testScheduler.advanceTimeBy(500, TimeUnit.MILLISECONDS)

        assertThat(viewModel.isErrorVisible, equalTo(true))
        assertThat(viewModel.errorMessage, equalTo("Sorry"))
    }

    @Test
    fun `The error message becomes invisible if a new request is send`(){
        val testScheduler = TestScheduler()
        val textObserver = PublishSubject.create<String>()

        val firstErrorThenDefault = object : Thesaurus {
            var count = 0;
            override fun findSynonym(word: String?): Observable<Synset> {
                return if (count == 0) {
                    count++
                    Observable.error(Exception("Sorry"))
                } else {
                   stubSynonyms.findSynonym(word)
                }
            }
        }
        val viewModel = SearchViewModel(firstErrorThenDefault, textObserver, testScheduler)

        textObserver.onNext("Test")
        testScheduler.advanceTimeBy(500, TimeUnit.MILLISECONDS)
        textObserver.onNext("Test2")
        testScheduler.advanceTimeBy(500, TimeUnit.MILLISECONDS)

        assertThat(viewModel.isErrorVisible, equalTo(false))
    }

}