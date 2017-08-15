package io.github.lmller.thesaurusrex.findsynonyms;


import io.github.lmller.thesaurusrex.Synset;
import io.reactivex.Observable;

public interface Thesaurus {
	Observable<Synset> findSynonym(String word);
}
