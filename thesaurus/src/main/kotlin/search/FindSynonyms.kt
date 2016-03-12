package search

import rx.Observable

class FindSynonyms(val source: SynonymSource) {

	fun findSynonyms(word: String): Observable<String>
	{
		return source.find(word).filter {
			!it.equals(word, true)
		}
	}

	interface SynonymSource {
		fun find(word: String): Observable<String>
	}

}