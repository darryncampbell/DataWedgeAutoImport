using System;
using Android.App;
using Android.Widget;
using Android.OS;
using Android.Support.V7.App;
using Android.Views;
using Android;
using Android.Content.PM;
using Java.IO;

namespace DataWedgeAutoImport
{
	[Activity(Label = "@string/app_name", Theme = "@style/AppTheme.NoActionBar", MainLauncher = true)]
	public class MainActivity : AppCompatActivity
	{

		protected override void OnCreate(Bundle savedInstanceState)
		{
			base.OnCreate(savedInstanceState);

			SetContentView(Resource.Layout.activity_main);

			Android.Support.V7.Widget.Toolbar toolbar = FindViewById<Android.Support.V7.Widget.Toolbar>(Resource.Id.toolbar);
            SetSupportActionBar(toolbar);

            if (Build.VERSION.SdkInt >= Android.OS.BuildVersionCodes.M)
            {
                if (CheckSelfPermission(Manifest.Permission.WriteExternalStorage) == (int)Permission.Granted)
                {
                    CreateTempFolder();
                }
                else
                {
                    output("Please grant file permission");
                    string [] permissions = { Manifest.Permission.WriteExternalStorage};
                    RequestPermissions(permissions, 0);
                }
            }

			Button importButton = FindViewById<Button>(Resource.Id.btnImport);
            importButton.Click += ImportClick;
		}

		public override bool OnCreateOptionsMenu(IMenu menu)
        {
            MenuInflater.Inflate(Resource.Menu.menu_main, menu);
            return true;
        }

        public override bool OnOptionsItemSelected(IMenuItem item)
        {
            int id = item.ItemId;
            if (id == Resource.Id.action_settings)
            {
                return true;
            }

            return base.OnOptionsItemSelected(item);
        }

        private void ImportClick(object sender, EventArgs eventArgs)
        {
            try
            {
                File externalStorageDirectory = Android.OS.Environment.GetExternalStoragePublicDirectory(Android.OS.Environment.DirectoryDocuments);
                File stagingDirectory = new File(externalStorageDirectory.Path, "/datawedge_import");
                File[] filesToStage = stagingDirectory.ListFiles();
                File outputDirectory = new File("/enterprise/device/settings/datawedge/autoimport");
                if (!outputDirectory.Exists())
                    outputDirectory.Mkdirs();
                if (filesToStage.Length == 0)
                    output("No files found in staging directory");

                for (int i = 0; i < filesToStage.Length; i++)
                {
                    File inputFile = filesToStage[i];
                    File outputFile = new File(outputDirectory, filesToStage[i].Name + ".tmp");
                    System.IO.File.Delete(outputFile.Path);
                    System.IO.File.Copy(inputFile.Path, outputFile.Path);
                    //  Rename the temp file
                    string renamedOutputFile = outputFile.Path.Substring(0, outputFile.Path.Length - 4);
                    outputFile.SetReadable(true, false);
                    outputFile.SetWritable(true, false);
                    outputFile.SetExecutable(true, false);
                    System.IO.File.Move(outputFile.Path, renamedOutputFile);
                    output("File(s) copied to DW autoimport directory");
                }
            }
            catch (FileNotFoundException e)
            {
                e.PrintStackTrace();
                output("File Not Found: " + e.Message);
            }
            catch (IOException e)
            {
                e.PrintStackTrace();
                output("Exception: " + e.Message);
            }
        }

        public override void OnRequestPermissionsResult(int requestCode, string[] permissions, Permission[] grantResults)
        {
            if (grantResults[0] == Permission.Granted)
            {
                CreateTempFolder();
            }
        }

        private Boolean CreateTempFolder()
        {
            File externalStorageDirectory = Android.OS.Environment.GetExternalStoragePublicDirectory(Android.OS.Environment.DirectoryDocuments);
            File stagingDirectory = new File(externalStorageDirectory.Path, "/datawedge_import");
            stagingDirectory.Mkdirs();
            TextView instructions = FindViewById<TextView>(Resource.Id.txtInstructions);
            instructions.Text = "Please copy all DataWedge profiles to the staging directory: adb push (file) " + stagingDirectory.Path;
            return true;
        }

        private void output(string output)
        {
            TextView outputTextView = FindViewById<TextView>(Resource.Id.txtOutput);
            outputTextView.Text = output;
        }
    }
}

