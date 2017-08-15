package io.github.lmller.thesaurusrex.findsynonyms;

import io.github.lmller.thesaurusrex.SynonymList;
import io.github.lmller.thesaurusrex.Synset;
import io.reactivex.Observable;
import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class OpenThesaurus implements Thesaurus {

	private final Api api;

	public OpenThesaurus(Api api) {
		this.api = api;
	}

	@Override
	public Observable<Synset> findSynonym(String word) {
		return api.find(word)
				.flatMapObservable(synonymList -> Observable.fromIterable(synonymList.getSynsets()));
	}

	public interface Api {
		@GET("synonyme/search?format=application/json")
		Single<SynonymList> find(@Query("q") String word);
	}
}
