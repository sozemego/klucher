package com.soze.kluch.dao;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
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
	public List<Kluch> findByAuthorIdInAndIdGreaterThan(Iterable<Long> authorIds, long greaterThanId, Pageable pageRequest) {
		String queryString = "SELECT k FROM Kluch k LEFT JOIN FETCH k.likes WHERE k.author.id IN (:ids) AND k.id > :previous";
		queryString += getOrderByString(pageRequest);
		TypedQuery<Kluch> query = em.createQuery(queryString, Kluch.class);
		query.setParameter("ids", authorIds);
		query.setParameter("previous", greaterThanId);
		query.setMaxResults(pageRequest.getPageSize());
		return query.getResultList();
	}
	
	@Override
	public List<Kluch> findByAuthorIdInAndIdLessThan(Iterable<Long> authorIds, long lessThanId, Pageable pageRequest) {
		String queryString = "SELECT k FROM Kluch k LEFT JOIN FETCH k.likes WHERE k.author.id IN (:ids) AND k.id < :next";
		queryString += getOrderByString(pageRequest);
		TypedQuery<Kluch> query = em.createQuery(queryString, Kluch.class);
		query.setParameter("ids", authorIds);
		query.setParameter("next", lessThanId);
		query.setMaxResults(pageRequest.getPageSize());
		return query.getResultList();
	}
	
	private String getOrderByString(Pageable pageRequest) {
		if(pageRequest == null || pageRequest.getSort() == null || pageRequest.getSort().getOrderFor("id") == null) {
			return "";
		}
		Direction dir = pageRequest.getSort().getOrderFor("id").getDirection();
		if(dir == Direction.ASC) {
			return " ORDER BY k.id ASC";
		}
		if(dir == Direction.DESC) {
			return " ORDER BY k.id DESC";
		}
		return "";
	}
	
	@Override
	public List<Kluch> findByMentionsInAndIdLessThan(String mention, long lessThanId, Pageable pageRequest) {
		return kluchRepository.findByMentionsInAndIdLessThan(mention, lessThanId, pageRequest);
	}
	
	@Override
	public List<Kluch> findByHashtagsInAndIdLessThan(String hashtag, long lessThanId, Pageable pageRequest) {
		return kluchRepository.findByHashtagsInAndIdLessThan(hashtag, lessThanId, pageRequest);
	}
	
	@Override
	@Cacheable(cacheNames = "kluchCount")
	public Long countByAuthorId(Long authorId) {
		String queryString = "SELECT COUNT(*) FROM Kluch k WHERE k.author.id = ?1";
		TypedQuery<Long> query = em.createQuery(queryString, Long.class);
		query.setParameter(1, authorId);
		return query.getSingleResult();
	}

	@Override
	public List<Kluch> findAllAfterTimestamp(Timestamp timestamp) {
		if(timestamp == null) {
			return new ArrayList<>();
		}
		String queryString = "SELECT k FROM Kluch k INNER JOIN FETCH k.hashtags WHERE k.timestamp > ?1";
		TypedQuery<Kluch> query = em.createQuery(queryString, Kluch.class);
		query.setParameter(1, timestamp);
		List<Kluch> kluchs = query.getResultList();
		return kluchs;
	}
	
	

}
