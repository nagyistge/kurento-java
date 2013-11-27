/*
 * (C) Copyright 2013 Kurento (http://kurento.org/)
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-2.1.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 */
package com.kurento.kmf.media.internal;

import static com.kurento.kms.thrift.api.KmsMediaHttpEndPointTypeConstants.CONSTRUCTOR_PARAMS_DATA_TYPE;
import static com.kurento.kms.thrift.api.KmsMediaHttpEndPointTypeConstants.EVENT_EOS_DETECTED;
import static com.kurento.kms.thrift.api.KmsMediaHttpEndPointTypeConstants.GET_URL;
import static com.kurento.kms.thrift.api.KmsMediaHttpEndPointTypeConstants.TYPE_NAME;

import java.util.Map;

import com.kurento.kmf.media.Continuation;
import com.kurento.kmf.media.HttpEndPoint;
import com.kurento.kmf.media.ListenerRegistration;
import com.kurento.kmf.media.MediaPipeline;
import com.kurento.kmf.media.MediaProfileSpecType;
import com.kurento.kmf.media.events.HttpEndPointEOSDetected;
import com.kurento.kmf.media.events.MediaEventListener;
import com.kurento.kmf.media.internal.refs.MediaElementRef;
import com.kurento.kmf.media.params.MediaParam;
import com.kurento.kmf.media.params.internal.HttpEndpointConstructorParam;
import com.kurento.kmf.media.params.internal.StringMediaParam;

@ProvidesMediaElement(type = TYPE_NAME)
public class HttpEndPointImpl extends AbstractSessionEndPoint implements
		HttpEndPoint {

	public HttpEndPointImpl(MediaElementRef endpointRef) {
		super(endpointRef);
	}

	/**
	 * @param objectRef
	 * @param params
	 */
	public HttpEndPointImpl(MediaElementRef objectRef,
			Map<String, MediaParam> params) {
		super(objectRef, params);
	}

	/* SYNC */
	@Override
	public String getUrl() {
		StringMediaParam result = (StringMediaParam) invoke(GET_URL);
		return result.getString();
	}

	@Override
	public ListenerRegistration addEOSDetectedListener(
			final MediaEventListener<HttpEndPointEOSDetected> sessionEvent) {
		return addListener(EVENT_EOS_DETECTED, sessionEvent);
	}

	/* ASYNC */

	@Override
	public void getUrl(final Continuation<String> cont) {
		invoke(GET_URL, new StringContinuationWrapper(cont));
	}

	@Override
	public void addEOSDetectedListener(
			final MediaEventListener<HttpEndPointEOSDetected> sessionEvent,
			final Continuation<ListenerRegistration> cont) {
		addListener(EVENT_EOS_DETECTED, sessionEvent, cont);
	}

	static class HttpEndPointBuilderImpl<T extends HttpEndPointBuilderImpl<T>>
			extends AbstractSessionEndPointBuilder<T, HttpEndPoint> implements
			HttpEndPointBuilder {

		// The param not stored in the map of params until some constructor
		// param is set.
		private final HttpEndpointConstructorParam param = new HttpEndpointConstructorParam();

		public HttpEndPointBuilderImpl(final MediaPipeline pipeline) {
			super(TYPE_NAME, pipeline);
		}

		public T withDisconnectionTimeout(int disconnectionTimeout) {
			param.setDisconnectionTimeout(Integer.valueOf(disconnectionTimeout));
			params.put(CONSTRUCTOR_PARAMS_DATA_TYPE, param);
			return self();
		}

		public T terminateOnEOS() {
			param.setTerminateOnEOS(Boolean.TRUE);
			params.put(CONSTRUCTOR_PARAMS_DATA_TYPE, param);
			return self();
		}

		public T withMediaProfile(MediaProfileSpecType type) {
			param.setMediaMuxer(type.toThrift());
			params.put(CONSTRUCTOR_PARAMS_DATA_TYPE, param);
			return self();
		}

	}

}