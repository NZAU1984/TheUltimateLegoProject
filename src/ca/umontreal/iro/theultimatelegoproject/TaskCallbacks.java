package ca.umontreal.iro.theultimatelegoproject;

public interface TaskCallbacks
{
	void onPreExecute(String fromWho);

	void onProgressUpdate(double fraction);

	void onCancelled();

	void onPostExecute(String fromWho, Boolean success);
}
