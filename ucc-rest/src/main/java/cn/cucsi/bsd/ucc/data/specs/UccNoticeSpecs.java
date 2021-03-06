package cn.cucsi.bsd.ucc.data.specs;

import cn.cucsi.bsd.ucc.common.beans.UccDomainCriteria;
import cn.cucsi.bsd.ucc.common.beans.UccNoticeCriteria;
import cn.cucsi.bsd.ucc.data.domain.UccDomain;
import cn.cucsi.bsd.ucc.data.domain.UccNotice;
import com.google.common.base.Strings;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import static org.springframework.data.jpa.domain.Specifications.where;

/**
 * Created by tianyuwei on 2017/10/16.
 */
public class UccNoticeSpecs {
    public static Specification<UccNotice> uccNoticeCodeEqual(final String noticeCode) {
        return new Specification<UccNotice>() {
            @Override
            public Predicate toPredicate(Root<UccNotice> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.equal(root.get("noticeCode"), noticeCode);
            }
        };
    }

    public static Specification<UccNotice> uccNoticeTitileLike(final String noticeTitle) {
        return new Specification<UccNotice>() {
            @Override
            public Predicate toPredicate(Root<UccNotice> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.like(root.<String>get("noticeTitle"), "%" + noticeTitle + "%");
            }
        };
    }
    
    public static Specification<UccNotice> uccNoticeUserIdEqual(final String userId) {
        return new Specification<UccNotice>() {
            @Override
            public Predicate toPredicate(Root<UccNotice> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.equal(root.<String>get("userId"), userId);
            }
        };
    }
    public static Specification<UccNotice> uccNoticeNoticeTypeEqual(final String noticeType) {
        return new Specification<UccNotice>() {
            @Override
            public Predicate toPredicate(Root<UccNotice> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.equal(root.<String>get("noticeType"), noticeType);
            }
        };
    }
    public static Specification<UccNotice> uccNoticeStartDateEqual(final Date from) {
        return new Specification<UccNotice>() {
            @Override
            public Predicate toPredicate(Root<UccNotice> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.lessThanOrEqualTo(root.<Date>get("startDate"), from);
            }
        };
    }
    public static Specification<UccNotice> uccNoticeEndDateEqual(final Date to) {
        return new Specification<UccNotice>() {
            @Override
            public Predicate toPredicate(Root<UccNotice> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.greaterThanOrEqualTo(root.<Date>get("endDate"), to);
            }
        };
    }
    public static Specification<UccNotice> domainIdEqual(final String domainId) {

        return new Specification<UccNotice>() {
            @Override
            public Predicate toPredicate(Root<UccNotice> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.equal(root.<String>get("domainId"), domainId);
            }
        };
    }
    public static Specification<UccNotice> createTimeThanOrEqualFrom(final Date from) {
        return new Specification<UccNotice>() {
            @Override
            public Predicate toPredicate(Root<UccNotice> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                try{
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
                    String strDate = sdf.format(from);
                    Date sDate=sdf.parse(strDate);
                    return criteriaBuilder.greaterThanOrEqualTo(root.<Date>get("createdTime"), sDate);
                }
                catch(Exception e){
                    e.printStackTrace();
                System.out.println("根据条件查询通话记录列表发生异常！");
                return null;
                }
            }
        };
    }
    
  
    
    public static Specification<UccNotice> createTimeLessOrEqualTo(final Date to) {
        return new Specification<UccNotice>() {
            @Override
            public Predicate toPredicate(Root<UccNotice> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                try{
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
                    String strDate = sdf.format(to);
                    Calendar calendar = new GregorianCalendar();
                    calendar.setTime(sdf.parse(strDate));
                    calendar.add(calendar.DATE,1);
                    Date sDate=calendar.getTime();
                    return criteriaBuilder.lessThanOrEqualTo(root.<Date>get("createdTime"), sDate);
                }
                catch(Exception e){
                    e.printStackTrace();
                System.out.println("根据条件查询通话记录列表发生异常！");
                return null;
                }
            }
        };
    }

    public static Specification<UccNotice> createSpec(final UccNoticeCriteria criteria) {

        Specification<UccNotice> spec = null;
        if(criteria==null) return spec;
        Specifications specs = where(spec);

        if(!Strings.isNullOrEmpty(criteria.getNoticeCode())){
            specs = specs.and(uccNoticeCodeEqual(criteria.getNoticeCode()));
        }

        if(!Strings.isNullOrEmpty(criteria.getNoticeTitle())){
            specs = specs.and(uccNoticeTitileLike(criteria.getNoticeTitle()));
        }
        if(!Strings.isNullOrEmpty(criteria.getUserId())){
            specs = specs.and(uccNoticeUserIdEqual(criteria.getUserId()));
        }
        if(!Strings.isNullOrEmpty(criteria.getNoticeType())){
            specs = specs.and(uccNoticeNoticeTypeEqual(criteria.getNoticeType()));
        }
        if(null!=criteria.getNoticeTimeFrom()){
            specs = specs.and(uccNoticeStartDateEqual(criteria.getNoticeTimeFrom()));
        }
        if(null != criteria.getNoticeTimeTo()){
            specs = specs.and(uccNoticeEndDateEqual(criteria.getNoticeTimeTo()));
        }
        if(!Strings.isNullOrEmpty(criteria.getDomainId())){
            specs = specs.and(domainIdEqual(criteria.getDomainId()));
        }
        if(null != criteria.getCreatedTimeTo()){
            specs = specs.and(createTimeLessOrEqualTo(criteria.getCreatedTimeTo()));
        }
         if(null != criteria.getCreatedTimeFrom()){
            specs = specs.and(createTimeThanOrEqualFrom(criteria.getCreatedTimeFrom()));
        }
        //System.out.println("2222");
        return specs;
    }
}
