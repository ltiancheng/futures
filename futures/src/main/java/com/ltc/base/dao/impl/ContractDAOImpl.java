package com.ltc.base.dao.impl;

import java.util.List;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.ltc.base.dao.ContractDAO;
import com.ltc.base.helpers.BaseConstant;
import com.ltc.base.vo.ContractMetaVO;
import com.ltc.base.vo.ContractVO;

public class ContractDAOImpl extends HibernateDaoSupport implements ContractDAO {

	@SuppressWarnings("unchecked")
	@Override
	public List<ContractVO> getActiveContractList() {
		String hql = "From ContractVO where status = :status";
		List<ContractVO> contractList = this.getSession().createQuery(hql)
				.setParameter("status", BaseConstant.ACTIVE).list();
		return contractList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ContractMetaVO> getContractMetaList() {
		return this.getSession().createCriteria(ContractMetaVO.class).list();
	}

}
