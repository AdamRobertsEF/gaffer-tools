/*
 * Copyright 2017 Crown Copyright
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.gchq.gaffer.slider.util;

import org.apache.hadoop.security.UserGroupInformation;
import org.apache.slider.api.ClusterDescription;
import org.apache.slider.api.ResourceKeys;
import org.apache.slider.client.SliderClient;
import org.apache.slider.core.conf.ConfTree;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class SliderUtils {

	public static void replaceTokens (ConfTree appConfig, String clusterName) throws IOException {
		// Replace ${USER} and ${USER_NAME}
		SliderClient.replaceTokens(appConfig, UserGroupInformation.getCurrentUser().getShortUserName(), clusterName);
		// Replace ${CLUSTER_NAME}
		replaceClusterTokens(appConfig, clusterName);
	}

	public static void replaceClusterTokens (ConfTree appConfig, String clusterName) {
		Map<String,String> newglobal = new HashMap<>();
		for (Map.Entry<String, String> entry : appConfig.global.entrySet()) {
			newglobal.put(entry.getKey(), replaceClusterTokens(entry.getValue(), clusterName));
		}
		appConfig.global.putAll(newglobal);

		for (String component : appConfig.components.keySet()) {
			Map<String, String> newComponent = new HashMap<>();
			for (Map.Entry<String, String> entry : appConfig.components.get(component).entrySet()) {
				newComponent.put(entry.getKey(), replaceClusterTokens(entry.getValue(), clusterName));
			}
			appConfig.components.get(component).putAll(newComponent);
		}

		Map<String,List<String>> newcred = new HashMap<>();
		for (Map.Entry<String, List<String>> entry : appConfig.credentials.entrySet()) {
			List<String> resultList = new ArrayList<>();
			for (String v : entry.getValue()) {
				resultList.add(replaceClusterTokens(v, clusterName));
			}
			newcred.put(replaceClusterTokens(entry.getKey(), clusterName), resultList);
		}
		appConfig.credentials.clear();
		appConfig.credentials.putAll(newcred);
	}

	public static String replaceClusterTokens (String input, String clusterName) {
		return input.replaceAll(Pattern.quote("${CLUSTER_NAME}"), clusterName);
	}

	/**
	 * Extracts the number of desired instances for each application component from a cluster's description
	 * @param clusterDescription Cluster description for an application instance
	 * @return The number of desired instances for each application component
	 */
	public static Map<String, Integer> getRoleMap (ClusterDescription clusterDescription) {
		Map<String, Integer> roleMap = new HashMap<String, Integer>();
		for (String role : clusterDescription.getRoleNames()) {
			int desiredInstances = clusterDescription.getRoleOptInt(role, ResourceKeys.COMPONENT_INSTANCES, 0);
			roleMap.put(role, desiredInstances);
		}
		return roleMap;
	}

}
