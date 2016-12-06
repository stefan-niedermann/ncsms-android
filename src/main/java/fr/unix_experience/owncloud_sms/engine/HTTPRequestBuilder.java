package fr.unix_experience.owncloud_sms.engine;

/*
 *  Copyright (c) 2014-2016, Loic Blot <loic.blot@unix-experience.fr>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import android.content.Context;
import android.net.Uri;

import com.owncloud.android.lib.common.OwnCloudClient;
import com.owncloud.android.lib.common.OwnCloudClientFactory;
import com.owncloud.android.lib.common.OwnCloudCredentialsFactory;

import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;

import java.io.IOException;

class HTTPRequestBuilder {

	private final OwnCloudClient _ocClient;

	// API v1 calls
	private static final String OC_GET_VERSION = "/index.php/apps/ocsms/get/apiversion?format=json";
	private static final String OC_GET_ALL_SMS_IDS = "/index.php/apps/ocsms/get/smsidlist?format=json";
	private static final String OC_GET_LAST_MSG_TIMESTAMP = "/index.php/apps/ocsms/get/lastmsgtime?format=json";
	private static final String OC_PUSH_ROUTE = "/index.php/apps/ocsms/push?format=json";

	// API v2 calls
	private static final String OC_V2_GET_PHONELIST = "/index.php/apps/ocsms/api/v2/phones/list?format=json";
	private static final String OC_V2_GET_MESSAGES ="/index.php/apps/ocsms/api/v2/messages/[START]/[LIMIT]?format=json";
	private static final String OC_V2_GET_MESSAGES_PHONE ="/index.php/apps/ocsms/api/v2/messages/[PHONENUMBER]/[START]/[LIMIT]?format=json";
	private static final String OC_V2_GET_MESSAGES_SENDQUEUE = "/index.php/apps/ocsms/api/v2/messages/sendqueue?format=json";

	HTTPRequestBuilder(Context context, Uri serverURI, String accountName, String accountPassword) {
		_ocClient = OwnCloudClientFactory.createOwnCloudClient(
				serverURI, context, true);

		// Set basic credentials
		_ocClient.setCredentials(
				OwnCloudCredentialsFactory.newBasicCredentials(accountName, accountPassword)
		);
	}

	private GetMethod get(String oc_call) {
		GetMethod get = new GetMethod(_ocClient.getBaseUri() + oc_call);
		get.addRequestHeader("OCS-APIREQUEST", "true");
		return get;
	}

	GetMethod getAllSmsIds() {
		return get(HTTPRequestBuilder.OC_GET_ALL_SMS_IDS);
	}

	public GetMethod getVersion() {
		return get(HTTPRequestBuilder.OC_GET_VERSION);
	}

	PostMethod pushSms(StringRequestEntity ent) {
		PostMethod post = new PostMethod(_ocClient.getBaseUri() + HTTPRequestBuilder.OC_PUSH_ROUTE);
		post.addRequestHeader("OCS-APIREQUEST", "true");
		post.setRequestEntity(ent);
		return post;
	}

	GetMethod getPhoneList() {
		return get(HTTPRequestBuilder.OC_V2_GET_PHONELIST);
	}

	GetMethod getMessages(Integer start, Integer limit) {
		return get(HTTPRequestBuilder.OC_V2_GET_MESSAGES.
				replace("[START]", start.toString()).replace("[LIMIT]", limit.toString()));
	}

	int execute(HttpMethod req) throws IOException {
		return _ocClient.executeMethod(req);
	}
}
