package data

import data.json.SynonymList
import data.json.Synset
import retrofit2.GsonConverterFactory
import retrofit2.Retrofit
import retrofit2.RxJavaCallAdapterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import rx.Observable
import rx.schedulers.Schedulers
import search.FindSynonyms

class OpenThesaurus : FindSynonyms.SynonymSource {

	//TODO inject
	private val retrofit = createRetrofit()
	private val api = retrofit.create(OpenThesaurusAPI::class.java)

	override fun find(word: String): Observable<String>
	{
		return api.searchSynonyms(word)
				.subscribeOn(Schedulers.io())
				.flatMapIterable {
					synonyms: SynonymList -> synonyms.synsets
				}.flatMapIterable {
					synset:Synset -> synset.terms
				}.map {
					term -> term.term
				}
	}

	private interface OpenThesaurusAPI {
		@GET("synonyme/search?format=application/json")
		fun searchSynonyms(@Query("q") word: String) : Observable<SynonymList>
	}

	private companion object {
		fun createRetrofit() : Retrofit {
			return Retrofit.Builder()
					.baseUrl("https://www.openthesaurus.de")
					.addCallAdapterFactory(RxJavaCallAdapterFactory.create())
					.addConverterFactory(GsonConverterFactory.create())
					.build()
		}
	}
}