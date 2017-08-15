package io.github.lmller.thesaurusrex

import io.github.lmller.thesaurusrex.findsynonyms.OpenThesaurus

import io.reactivex.Single
import io.reactivex.observers.TestObserver
import org.junit.Test

class OpenThesaurusTest {

    val stubApi = OpenThesaurus.Api {
        val expectedTerms1 = listOf(Term("Erprobung"), Term("Probe"), Term("Prüfung"))
        val expectedTerms2 = listOf(Term("Kontrolle"), Term("Leistungsnachweis"))
        val sets = listOf(
                Synset(1L, terms=expectedTerms1),
                Synset(2L, terms=expectedTerms2)
        )
        Single.just(SynonymList(synsets = sets))
    }

    @Test
    fun `Open thesaurus finds a stream of Synsets`() {
        val openThesaurus = OpenThesaurus(stubApi)

        val subscriber = TestObserver<Synset>()

        openThesaurus.findSynonym("test")
                .subscribe(subscriber)

        subscriber.assertValueCount(2)
        subscriber.assertValueAt(0) { it.id == 1L }
        subscriber.assertValueAt(1) { it.id == 2L }

    }
}