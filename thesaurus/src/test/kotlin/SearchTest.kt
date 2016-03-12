package test;

import org.junit.Before
import org.junit.Test
import rx.Observable
import search.FindSynonyms

class SearchTest {

	val testSubscriber = rx.observers.TestSubscriber<String>()
	var source: DummySynonyms? = null
	var finder: FindSynonyms? = null

	@Before
	fun setUp(){
		 source = DummySynonyms()
		 finder = FindSynonyms(source as DummySynonyms)
	}

	@Test
	fun Find_Synonyms_finds_all_synonyms(){
		finder!!.findSynonyms("Gruß").subscribe(testSubscriber)

		testSubscriber.assertValues("Begrüßung", "Salut", "Wilkommenheißung")
	}

	@Test
	fun Found_Synonyms_does_not_contain_the_searched_word_as_synonym(){
		source!!.add("Gruß", "Gruß")
		finder!!.findSynonyms("Gruß").subscribe(testSubscriber)

		testSubscriber.assertValues("Begrüßung", "Salut", "Wilkommenheißung")
	}

	class DummySynonyms : FindSynonyms.SynonymSource
	{
		val testDictionary = mutableMapOf(
				"gruß" to mutableListOf("Begrüßung", "Salut", "Wilkommenheißung")
		)

		fun add(word: String, synonym: String){
			testDictionary[word.toLowerCase()]?.add(synonym);
		}

		override fun find(word: String): Observable<String>
		{
			return Observable.from(testDictionary[word.toLowerCase()])
		}

	}

}