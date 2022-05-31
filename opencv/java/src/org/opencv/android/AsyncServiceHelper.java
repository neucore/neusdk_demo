package org.opencv.android;

import java.io.File;
import java.util.StringTokenizer;

import org.opencv.core.Core;
import org.opencv.engine.OpenCVEngineInterface;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.IBinder;
import android.os.RemoteException;

import com.blankj.utilcode.util.LogUtils;

class AsyncServiceHelper
{
    public static boolean initOpenCV(String Version, final Context AppContext,
            final LoaderCallbackInterface Callback)
    {
        AsyncServiceHelper helper = new AsyncServiceHelper(Version, AppContext, Callback);
        Intent intent = new Intent("org.opencv.engine.BIND");
        intent.setPackage("org.opencv.engine");
        if (AppContext.bindService(intent, helper.mServiceConnection, Context.BIND_AUTO_CREATE))
        {
            return true;
        }
        else
        {
            AppContext.unbindService(helper.mServiceConnection);
            InstallService(AppContext, Callback);
            return false;
        }
    }

    protected AsyncServiceHelper(String Version, Context AppContext, LoaderCallbackInterface Callback)
    {
        mOpenCVersion = Version;
        mUserAppCallback = Callback;
        mAppContext = AppContext;
    }

    protected static final String TAG = "OpenCVManager/Helper";
    protected static final int MINIMUM_ENGINE_VERSION = 2;
    protected OpenCVEngineInterface mEngineService;
    protected LoaderCallbackInterface mUserAppCallback;
    protected String mOpenCVersion;
    protected Context mAppContext;
    protected static boolean mServiceInstallationProgress = false;
    protected static boolean mLibraryInstallationProgress = false;

    protected static boolean InstallServiceQuiet(Context context)
    {
        boolean result = true;
        try
        {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(OPEN_CV_SERVICE_URL));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
        catch(Exception e)
        {
            result = false;
        }

        return result;
    }

    protected static void InstallService(final Context AppContext, final LoaderCallbackInterface Callback)
    {
        if (!mServiceInstallationProgress)
        {
                LogUtils.dTag(TAG, "Request new service installation");
                InstallCallbackInterface InstallQuery = new InstallCallbackInterface() {
                private LoaderCallbackInterface mUserAppCallback = Callback;
                public String getPackageName()
                {
                    return "OpenCV Manager";
                }
                public void install() {
                    LogUtils.dTag(TAG, "Trying to install OpenCV Manager via Google Play");

                    boolean result = InstallServiceQuiet(AppContext);
                    if (result)
                    {
                        mServiceInstallationProgress = true;
                        LogUtils.dTag(TAG, "Package installation started");
                    }
                    else
                    {
                        LogUtils.dTag(TAG, "OpenCV package was not installed!");
                        int Status = LoaderCallbackInterface.MARKET_ERROR;
                        LogUtils.dTag(TAG, "Init finished with status " + Status);
                        LogUtils.dTag(TAG, "Unbind from service");
                        LogUtils.dTag(TAG, "Calling using callback");
                        mUserAppCallback.onManagerConnected(Status);
                    }
                }

                public void cancel()
                {
                    LogUtils.dTag(TAG, "OpenCV library installation was canceled");
                    int Status = LoaderCallbackInterface.INSTALL_CANCELED;
                    LogUtils.dTag(TAG, "Init finished with status " + Status);
                    LogUtils.dTag(TAG, "Calling using callback");
                    mUserAppCallback.onManagerConnected(Status);
                }

                public void wait_install()
                {
                    LogUtils.e(TAG, "Installation was not started! Nothing to wait!");
                }
            };

            Callback.onPackageInstall(InstallCallbackInterface.NEW_INSTALLATION, InstallQuery);
        }
        else
        {
            LogUtils.dTag(TAG, "Waiting current installation process");
            InstallCallbackInterface WaitQuery = new InstallCallbackInterface() {
                private LoaderCallbackInterface mUserAppCallback = Callback;
                public String getPackageName()
                {
                    return "OpenCV Manager";
                }
                public void install()
                {
                    LogUtils.e(TAG, "Nothing to install we just wait current installation");
                }
                public void cancel()
                {
                    LogUtils.dTag(TAG, "Waiting for OpenCV canceled by user");
                    mServiceInstallationProgress = false;
                    int Status = LoaderCallbackInterface.INSTALL_CANCELED;
                    LogUtils.dTag(TAG, "Init finished with status " + Status);
                    LogUtils.dTag(TAG, "Calling using callback");
                    mUserAppCallback.onManagerConnected(Status);
                }
                public void wait_install()
                {
                     InstallServiceQuiet(AppContext);
                }
            };

            Callback.onPackageInstall(InstallCallbackInterface.INSTALLATION_PROGRESS, WaitQuery);
        }
    }

    /**
     *  URL of OpenCV Manager page on Google Play Market.
     */
    protected static final String OPEN_CV_SERVICE_URL = "market://details?id=org.opencv.engine";

    protected ServiceConnection mServiceConnection = new ServiceConnection()
    {
        public void onServiceConnected(ComponentName className, IBinder service)
        {
            LogUtils.dTag(TAG, "Service connection created");
            mEngineService = OpenCVEngineInterface.Stub.asInterface(service);
            if (null == mEngineService)
            {
                LogUtils.dTag(TAG, "OpenCV Manager Service connection fails. May be service was not installed?");
                InstallService(mAppContext, mUserAppCallback);
            }
            else
            {
                mServiceInstallationProgress = false;
                try
                {
                    if (mEngineService.getEngineVersion() < MINIMUM_ENGINE_VERSION)
                    {
                        LogUtils.dTag(TAG, "Init finished with status " + LoaderCallbackInterface.INCOMPATIBLE_MANAGER_VERSION);
                        LogUtils.dTag(TAG, "Unbind from service");
                        mAppContext.unbindService(mServiceConnection);
                        LogUtils.dTag(TAG, "Calling using callback");
                        mUserAppCallback.onManagerConnected(LoaderCallbackInterface.INCOMPATIBLE_MANAGER_VERSION);
                        return;
                    }

                    LogUtils.dTag(TAG, "Trying to get library path");
                    String path = mEngineService.getLibPathByVersion(mOpenCVersion);
                    if ((null == path) || (path.length() == 0))
                    {
                        if (!mLibraryInstallationProgress)
                        {
                            InstallCallbackInterface InstallQuery = new InstallCallbackInterface() {
                                public String getPackageName()
                                {
                                    return "OpenCV library";
                                }
                                public void install() {
                                    LogUtils.dTag(TAG, "Trying to install OpenCV lib via Google Play");
                                    try
                                    {
                                        if (mEngineService.installVersion(mOpenCVersion))
                                        {
                                            mLibraryInstallationProgress = true;
                                            LogUtils.dTag(TAG, "Package installation started");
                                            LogUtils.dTag(TAG, "Unbind from service");
                                            mAppContext.unbindService(mServiceConnection);
                                        }
                                        else
                                        {
                                            LogUtils.dTag(TAG, "OpenCV package was not installed!");
                                            LogUtils.dTag(TAG, "Init finished with status " + LoaderCallbackInterface.MARKET_ERROR);
                                            LogUtils.dTag(TAG, "Unbind from service");
                                            mAppContext.unbindService(mServiceConnection);
                                            LogUtils.dTag(TAG, "Calling using callback");
                                            mUserAppCallback.onManagerConnected(LoaderCallbackInterface.MARKET_ERROR);
                                        }
                                    } catch (RemoteException e) {
                                        e.printStackTrace();;
                                        LogUtils.dTag(TAG, "Init finished with status " + LoaderCallbackInterface.INIT_FAILED);
                                        LogUtils.dTag(TAG, "Unbind from service");
                                        mAppContext.unbindService(mServiceConnection);
                                        LogUtils.dTag(TAG, "Calling using callback");
                                        mUserAppCallback.onManagerConnected(LoaderCallbackInterface.INIT_FAILED);
                                    }
                                }
                                public void cancel() {
                                    LogUtils.dTag(TAG, "OpenCV library installation was canceled");
                                    LogUtils.dTag(TAG, "Init finished with status " + LoaderCallbackInterface.INSTALL_CANCELED);
                                    LogUtils.dTag(TAG, "Unbind from service");
                                    mAppContext.unbindService(mServiceConnection);
                                    LogUtils.dTag(TAG, "Calling using callback");
                                    mUserAppCallback.onManagerConnected(LoaderCallbackInterface.INSTALL_CANCELED);
                                }
                                public void wait_install() {
                                    LogUtils.e(TAG, "Installation was not started! Nothing to wait!");
                                }
                            };

                            mUserAppCallback.onPackageInstall(InstallCallbackInterface.NEW_INSTALLATION, InstallQuery);
                        }
                        else
                        {
                            InstallCallbackInterface WaitQuery = new InstallCallbackInterface() {
                                public String getPackageName()
                                {
                                    return "OpenCV library";
                                }

                                public void install() {
                                    LogUtils.e(TAG, "Nothing to install we just wait current installation");
                                }
                                public void cancel()
                                {
                                    LogUtils.dTag(TAG, "OpenCV library installation was canceled");
                                    mLibraryInstallationProgress = false;
                                    LogUtils.dTag(TAG, "Init finished with status " + LoaderCallbackInterface.INSTALL_CANCELED);
                                    LogUtils.dTag(TAG, "Unbind from service");
                                    mAppContext.unbindService(mServiceConnection);
                                    LogUtils.dTag(TAG, "Calling using callback");
                                        mUserAppCallback.onManagerConnected(LoaderCallbackInterface.INSTALL_CANCELED);
                                }
                                public void wait_install() {
                                    LogUtils.dTag(TAG, "Waiting for current installation");
                                    try
                                    {
                                        if (!mEngineService.installVersion(mOpenCVersion))
                                        {
                                            LogUtils.dTag(TAG, "OpenCV package was not installed!");
                                            LogUtils.dTag(TAG, "Init finished with status " + LoaderCallbackInterface.MARKET_ERROR);
                                            LogUtils.dTag(TAG, "Calling using callback");
                                            mUserAppCallback.onManagerConnected(LoaderCallbackInterface.MARKET_ERROR);
                                        }
                                        else
                                        {
                                            LogUtils.dTag(TAG, "Wating for package installation");
                                        }

                                        LogUtils.dTag(TAG, "Unbind from service");
                                        mAppContext.unbindService(mServiceConnection);

                                    } catch (RemoteException e) {
                                        e.printStackTrace();
                                        LogUtils.dTag(TAG, "Init finished with status " + LoaderCallbackInterface.INIT_FAILED);
                                        LogUtils.dTag(TAG, "Unbind from service");
                                        mAppContext.unbindService(mServiceConnection);
                                        LogUtils.dTag(TAG, "Calling using callback");
                                        mUserAppCallback.onManagerConnected(LoaderCallbackInterface.INIT_FAILED);
                                    }
                               }
                            };

                            mUserAppCallback.onPackageInstall(InstallCallbackInterface.INSTALLATION_PROGRESS, WaitQuery);
                        }
                        return;
                    }
                    else
                    {
                        LogUtils.dTag(TAG, "Trying to get library list");
                        mLibraryInstallationProgress = false;
                        String libs = mEngineService.getLibraryList(mOpenCVersion);
                        LogUtils.dTag(TAG, "Library list: \"" + libs + "\"");
                        LogUtils.dTag(TAG, "First attempt to load libs");
                        int status;
                        if (initOpenCVLibs(path, libs))
                        {
                            LogUtils.dTag(TAG, "First attempt to load libs is OK");
                            String eol = System.getProperty("line.separator");
                            for (String str : Core.getBuildInformation().split(eol))
                                LogUtils.i(TAG, str);

                            status = LoaderCallbackInterface.SUCCESS;
                        }
                        else
                        {
                            LogUtils.dTag(TAG, "First attempt to load libs fails");
                            status = LoaderCallbackInterface.INIT_FAILED;
                        }

                        LogUtils.dTag(TAG, "Init finished with status " + status);
                        LogUtils.dTag(TAG, "Unbind from service");
                        mAppContext.unbindService(mServiceConnection);
                        LogUtils.dTag(TAG, "Calling using callback");
                        mUserAppCallback.onManagerConnected(status);
                    }
                }
                catch (RemoteException e)
                {
                    e.printStackTrace();
                    LogUtils.dTag(TAG, "Init finished with status " + LoaderCallbackInterface.INIT_FAILED);
                    LogUtils.dTag(TAG, "Unbind from service");
                    mAppContext.unbindService(mServiceConnection);
                    LogUtils.dTag(TAG, "Calling using callback");
                    mUserAppCallback.onManagerConnected(LoaderCallbackInterface.INIT_FAILED);
                }
            }
        }

        public void onServiceDisconnected(ComponentName className)
        {
            mEngineService = null;
        }
    };

    private boolean loadLibrary(String AbsPath)
    {
        boolean result = true;

        LogUtils.dTag(TAG, "Trying to load library " + AbsPath);
        try
        {
            System.load(AbsPath);
            LogUtils.dTag(TAG, "OpenCV libs init was ok!");
        }
        catch(UnsatisfiedLinkError e)
        {
            LogUtils.dTag(TAG, "Cannot load library \"" + AbsPath + "\"");
            e.printStackTrace();
            result = false;
        }

        return result;
    }

    private boolean initOpenCVLibs(String Path, String Libs)
    {
        LogUtils.dTag(TAG, "Trying to init OpenCV libs");
        if ((null != Path) && (Path.length() != 0))
        {
            boolean result = true;
            if ((null != Libs) && (Libs.length() != 0))
            {
                LogUtils.dTag(TAG, "Trying to load libs by dependency list");
                StringTokenizer splitter = new StringTokenizer(Libs, ";");
                while(splitter.hasMoreTokens())
                {
                    String AbsLibraryPath = Path + File.separator + splitter.nextToken();
                    result &= loadLibrary(AbsLibraryPath);
                }
            }
            else
            {
                // If the dependencies list is not defined or empty.
                String AbsLibraryPath = Path + File.separator + "libopencv_java4.so";
                result = loadLibrary(AbsLibraryPath);
            }

            return result;
        }
        else
        {
            LogUtils.dTag(TAG, "Library path \"" + Path + "\" is empty");
            return false;
        }
    }
}
