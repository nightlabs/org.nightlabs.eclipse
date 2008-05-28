package org.nightlabs.base.ui.login;

/**
 * A value of this enum indicates the current login-state of the client application. The application
 * starts initially in the {@link #LOGGED_OUT} state.
 * <p>
 * When a login is triggered, the login process has these steps:
 * <ul>
 * <li>First, the {@link LoginState} switches to {@link #ABOUT_TO_LOG_IN} and all listeners are notified about this transition.</li>
 * <li>Then the actual login happens. This usually includes a login-dialog popping up.</li>
 * <li>
 * Finally, there are two possibilities:
 *		<ul>
 *			<li>Either the login completed successfully and the state switches to {@link #LOGGED_IN}</li>
 *			<li>or the user aborted the login (e.g. clicking a cancel-button in the login-dialog) and the state will change to {@link #LOGGED_OUT} again.</li>
 *		</ul>
 * </li>
 * </ul>
 * </p>
 * <p>
 * During logout, these steps happen:
 * <ul>
 * <li>First, the {@link LoginState} switches to {@link #ABOUT_TO_LOG_IN} and all listeners are notified about this transition.</li>
 * <li>Then the actual logout happens. During this step, internal clean-up work is usually done (like removing listeners) and normally no user interaction occurs.</li>
 * <li>Finally, the state switches to {@link #LOGGED_OUT}.</li>
 * </ul>
 * </p>
 * 
 * @author marco schulze - marco at nightlabs dot de
 */
public enum LoginState {
	/**
	 * The user is not logged in - hence, there is no user information available and communication with the server is not possible.
	 * This is the initial state when the client application has been started and it is the end state when the user manually
	 * logs out (by clicking a logout button). 
	 */
	LOGGED_OUT,

	/**
	 * The login-process has been started. At this moment, the user is not yet known and therefore communication with the server is
	 * not yet possible.
	 * <p>
	 * This is a transitional state (usually not pending long time, only as long as the user doesn't click the login-dialog away) and
	 * the next state is either {@link #LOGGED_IN} or {@link #LOGGED_OUT} depending on whether the login is completed successfully or
	 * aborted by the user. The login-state does not change while the actual authentication process is trying to authenticate at the
	 * server. Hence, if the authentication fails, the state still is {@link #ABOUT_TO_LOG_IN} and a login-dialog stays open until the user
	 * gives up.
	 * </p>
	 */
	ABOUT_TO_LOG_IN,

	/**
	 * A current user is known and authenticated. Communication with the server is possible. This is a permanent state and changes
	 * only when the user closes the application or clicks a logout button. It may change, too, after a certain inactivity timeout.
	 * Such a feature is not yet implemented, but may come in the future.
	 */
	LOGGED_IN,

	/**
	 * The logout-process has been started. At this moment, the user is still known and thus communication with the server is still
	 * possible.
	 * <p>
	 * This is a transitional state and the next state is {@link #LOGGED_OUT}.
	 * </p>
	 */
	ABOUT_TO_LOG_OUT,
}
