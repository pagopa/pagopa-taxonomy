echo "Running tests..."
rm -rf report reports

behave --format html -o reports/index.html --summary --show-timings -v