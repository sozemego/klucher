package com.soze.kluch.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.soze.kluch.model.Kluch;
import com.soze.kluch.repository.KluchRepository;

@Service
public class KluchDatabase implements KluchDao {

	private final KluchRepository kluchRepository;
	private final EntityManager em;

	@Autowired
	public KluchDatabase(KluchRepository kluchRepository, EntityManager em) {
		this.kluchRepository = kluchRepository;
		this.em = em;
	}

	@Override
	@CacheEvict(cacheNames = "kluchCount", key = "#kluch.author.id")
	public Kluch save(Kluch kluch) {
		return kluchRepository.save(kluch);
	}

	@Override
	public Iterable<Kluch> save(Iterable<Kluch> kluchs) {
		return kluchRepository.save(kluchs);
	}

	@Override
	public Kluch findOne(long id) {
		return kluchRepository.findOne(id);
	}

	@Override
	public List<Kluch> findAll(Iterable<Long> ids) {
		return kluchRepository.findAll(ids);
	}

	@Override
	public long count() {
		return kluchRepository.count();
	}

	@Override
	public void delete(long id) {
		kluchRepository.delete(id);
	}

	@Override
	public void delete(Kluch kluch) {
		kluchRepository.delete(kluch);
	}

	@Override
	public void delete(Iterable<Kluch> kluchs) {
		kluchRepository.delete(kluchs);
	}

	@Override
	public void deleteAll() {
		kluchRepository.deleteAll();
	}

	@Override
	public Page<Kluch> findByAuthorIdInAndIdGreaterThan(Iterable<Long> authorIds, long greaterThanId, Pageable pageRequest) {
		String queryString = "SELECT k FSROM Kluch k WHERE k.author.id IN (:ids) AND k.id > :previous";
		TypedQuery<Kluch> query = em.createQuery(queryString, Kluch.class);
		query.setParameter("ids", authorIds);
		query.setParameter("previous", greaterThanId);
		query.setMaxResults(pageRequest.getPageSize());
		return new PageImpl<>(query.getResultList());
	}
	
	@Override
	public Page<Kluch> findByAuthorIdInAndIdLessThan(Iterable<Long> authorIds, long lessThanId, Pageable pageRequest) {
		String queryString = "SELECT k FSROM Kluch k WHERE k.author.id IN (:ids) AND k.id < :next";
		TypedQuery<Kluch> query = em.createQuery(queryString, Kluch.class);
		query.setParameter("ids", authorIds);
		query.setParameter("next", lessThanId);
		query.setMaxResults(pageRequest.getPageSize());
		List<Kluch> kluchs = query.getResultList();
		return new PageImpl<>(kluchs);
	}
	
	@Override
	public Page<Kluch> findByMentionsInAndIdLessThan(String mention, long lessThanId, Pageable pageRequest) {
		return kluchRepository.findByMentionsInAndIdLessThan(mention, lessThanId, pageRequest);
	}
	
	@Override
	public Page<Kluch> findByHashtagsInAndIdLessThan(String hashtag, long lessThanId, Pageable pageRequest) {
		return kluchRepository.findByHashtagsInAndIdLessThan(hashtag, lessThanId, pageRequest);
	}
	
	@Override
	@Cacheable(cacheNames = "kluchCount")
	public Long countByAuthorId(Long authorId) {
		String queryString = "SELECT COUNT(*) FROM Kluch k WHERE k.id = ?1";
		TypedQuery<Long> query = em.createQuery(queryString, Long.class);
		query.setParameter(1, authorId);
		return query.getSingleResult();
	}

}
