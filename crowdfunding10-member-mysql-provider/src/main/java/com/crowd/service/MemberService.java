package com.crowd.service;

import com.crowd.entity.po.MemberOP;

public interface MemberService {
	
	 MemberOP getMemberOPByLoginAcctRemote(String loginAcct);

	void saveMember(MemberOP memberOP);
}
