echo "Running tests..."
rm -rf report reports
rm -rf taxonomy.csv

behave --format html -o reports/index.html --summary --show-timings -v